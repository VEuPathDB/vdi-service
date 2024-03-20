package org.veupathdb.vdi.lib.reconciler

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.CoroutineThreadContext
import org.apache.logging.log4j.kotlin.ThreadContextData
import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.reconciler.config.ReconcilerConfig
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ReconcilerImpl(private val config: ReconcilerConfig) : Reconciler, VDIServiceModuleBase("reconciler") {

  private val wakeInterval = 2.seconds

  override suspend fun run() {
    if (!config.reconcilerEnabled) {
      logger().warn("Reconciler is disabled. Skipping run")
      return
    }

    logger().info("Running ReconcilerImpl module")

    val datasetManager = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kafkaRouter = requireKafkaRouter()

    val targets: MutableList<ReconcilerInstance> = AppDatabaseRegistry.iterator().asSequence()
      .map { (project, _) ->
        ReconcilerInstance(
          AppDBTarget(project, project),
          datasetManager,
          kafkaRouter
        )
      }
      .toMutableList()

    targets.add(ReconcilerInstance(CacheDBTarget(), datasetManager, kafkaRouter))

    runBlocking {
      // Last run to zero to perform a reconciliation on service startup.
      var lastRun = 0L

      while (!isShutDown()) {
        // Wake up on a short interval to catch shutdown signals.
        delay(wakeInterval)

        val now = System.currentTimeMillis()

        // If we haven't yet reached the configured amount of time for the run
        // interval, skip to the next iteration, sleep, and test again.
        if ((now - lastRun).milliseconds < config.runInterval) {
          continue
        }

        lastRun = now

        // If we've reached this point, we've waited for the configured interval
        // duration.  We can now run the reconciliation process.
        logger().info("Scheduling reconciler for ${targets.size} targets.")

        val timer = Metrics.Reconciler.reconcilerTimes.startTimer()

        // Schedule the reconciler for each target database.
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

        timer.observeDuration()
      }
    }

    logger().info("closing kafka router")
    kafkaRouter.close()
    logger().info("kafka router closed")
    confirmShutdown()
  }

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
  }
}