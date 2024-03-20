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
import vdi.daemon.reconciler.config.ReconcilerConfig
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class ReconcilerImpl(private val config: ReconcilerConfig) : Reconciler, AbstractJobExecutor("reconciler") {
  private var datasetManager: DatasetManager

  private var kafkaRouter: KafkaRouter

  private val targets: List<ReconcilerInstance>

  private var lastRun = 0L

  init {
    runBlocking {
      datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
      kafkaRouter = requireKafkaRouter()
    }

    targets = AppDatabaseRegistry.iterator().asSequence()
      .map { (project, _) ->
        ReconcilerInstance(
          AppDBTarget(project, project),
          datasetManager,
          kafkaRouter
        )
      }
      .toMutableList()

    targets.add(ReconcilerInstance(CacheDBTarget(), datasetManager, kafkaRouter))
  }

  override suspend fun runJob() {
    if (!config.reconcilerEnabled) {
      logger().warn("Reconciler is disabled. Skipping run")
      return
    }

    val now = System.currentTimeMillis()

    // If we haven't yet reached the configured amount of time for the run
    // interval, skip to the next iteration, sleep, and test again.
    if ((now - lastRun).milliseconds < config.runInterval) {
      return
    }

    lastRun = now

    // If we've reached this point, we've waited for the configured interval
    // duration.  We can now run the reconciliation process.
    logger().info("Scheduling reconciler for ${targets.size} targets.")

    val timer = Metrics.Reconciler.reconcilerTimes.startTimer()

    // Schedule the reconciler for each target database.
    coroutineScope {
      targets.forEach { target ->
        launch(
          CoroutineThreadContext(
            contextData = ThreadContextData( // Add log4j context to reconciler to distinguish targets in logs.
              map = mapOf(
                Pair(
                  "workerID",
                  target.name
                )
              ), Stack()
            )
          )
        ) { target.reconcile() }
      }
    }

    timer.observeDuration()
  }

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
  }
}