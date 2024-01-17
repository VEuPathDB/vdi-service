package org.veupathdb.vdi.lib.reconciler

import kotlinx.coroutines.*
import org.apache.logging.log4j.kotlin.CoroutineThreadContext
import org.apache.logging.log4j.kotlin.ThreadContextData
import org.apache.logging.log4j.kotlin.logger
import org.veupathdb.vdi.lib.db.app.AppDatabaseRegistry
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.reconciler.config.ReconcilerConfig
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase
import java.util.*

class ReconcilerImpl(private val config: ReconcilerConfig) :
  Reconciler, VDIServiceModuleBase("reconciler") {

  override suspend fun run() {
    if (!config.reconcilerEnabled) {
      logger().warn("Reconciler is disabled. Skipping run")
      return
    }

    logger().info("Running ReconcilerImpl module")

    val datasetManager = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
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
      while (!isShutDown()) {
        logger().info("Scheduling reconciler for ${targets.size} targets.")

        val timer = Metrics.reconcilerTimes.startTimer()

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

        delay(config.runInterval.toMillis())
      }
    }
  }

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
  }
}