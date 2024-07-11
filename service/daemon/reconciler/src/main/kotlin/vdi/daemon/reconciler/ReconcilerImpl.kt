package vdi.daemon.reconciler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.CoroutineThreadContext
import org.apache.logging.log4j.kotlin.ThreadContextData
import org.apache.logging.log4j.kotlin.logger
import vdi.component.db.app.AppDatabaseRegistry
import vdi.component.kafka.router.KafkaRouter
import vdi.component.kafka.router.KafkaRouterFactory
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractJobExecutor
import vdi.component.s3.DatasetManager
import kotlin.time.Duration.Companion.milliseconds

internal class ReconcilerImpl(private val config: ReconcilerConfig, abortCB: (String?) -> Nothing)
  : Reconciler
  , AbstractJobExecutor("reconciler", abortCB)
{
  private var datasetManager: DatasetManager

  private var kafkaRouter: KafkaRouter

  private val targets: List<ReconcilerTarget>

  private var lastSlimRun = 0L

  private var lastFullRun = 0L

  private val cacheDBTarget: ReconcilerTarget

  init {
    runBlocking {
      datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
      kafkaRouter = requireKafkaRouter()
    }

    targets = ArrayList(AppDatabaseRegistry.size() + 1)

    AppDatabaseRegistry.iterator().asSequence()
      .map { (project, _) -> AppDBTarget(project, project) }
      .forEach { targets.add(it) }

    cacheDBTarget = CacheDBTarget()

    targets.add(cacheDBTarget)
  }

  override suspend fun runJob() {
    val now = System.currentTimeMillis()

    when {
      // delta between last full run and now is greater than or equal to the
      // full reconciler run interval
      (now - lastFullRun).milliseconds >= config.fullRunInterval -> {
        lastFullRun = now
        runFull()
      }

      // delta between last slim run and now is greater than or equal to the
      // slim reconciler run interval
      (now - lastSlimRun).milliseconds >= config.slimRunInterval -> {
        lastSlimRun = now
        runSlim()
      }
    }
  }

  private suspend fun runSlim() {
    logger().info("Scheduling slim reconciler.")

    val timer = Metrics.Reconciler.Slim.executionTime.startTimer()

    coroutineScope {
      launch(CoroutineThreadContext(ThreadContextData(mapOf("workerID" to workerName(true, cacheDBTarget))))) {
        ReconcilerInstance(cacheDBTarget, datasetManager, kafkaRouter, true).reconcile()
      }
    }

    timer.observeDuration()
  }

  private suspend fun runFull() {
    logger().info("Scheduling reconciler for ${targets.size} targets.")

    val timer = Metrics.Reconciler.Full.reconcilerTimes.startTimer()

    // Schedule the reconciler for each target database.
    coroutineScope {
      targets.forEach {
        launch(CoroutineThreadContext(ThreadContextData(mapOf("workerID" to workerName(false, it))))) {
          ReconcilerInstance(it, datasetManager, kafkaRouter, false).reconcile()
        }
      }
    }

    timer.observeDuration()
  }

  private fun workerName(slim: Boolean, tgt: ReconcilerTarget) = if (slim) "slim-${tgt.name}" else "full-${tgt.name}"

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
  }
}