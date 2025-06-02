package vdi.lib.reconciler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.veupathdb.lib.s3.s34k.S3Api
import vdi.lib.db.app.AppDatabaseRegistry
import vdi.lib.kafka.router.KafkaRouter
import vdi.lib.kafka.router.KafkaRouterFactory
import vdi.core.logging.logger
import vdi.core.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.s3.DatasetObjectStore

object Reconciler {
  private val log = logger()

  private val config = ReconcilerConfig()

  private val active = Mutex()

  private var initialized = false

  private lateinit var datasetManager: DatasetObjectStore

  private lateinit var kafkaRouter: KafkaRouter

  fun initialize(abortCB: AbortCB) {
    // We lock while initializing to avoid double init.
    if (active.isLocked)
      return

    // If we've already successfully initialized, nothing to do.
    if (initialized)
      return

    // Initialize
    runBlocking {
      active.withLock {
        if (AppDatabaseRegistry.isBroken) {
          log.error("refusing to start reconciler, app db registry is in a bad state")
          abortCB("app db registry in bad state")
        }

        try {
          val s3 = S3Api.newClient(config.s3Config)
          val bucket = s3.buckets[config.s3Bucket]
            ?: throw IllegalStateException("S3 bucket ${config.s3Bucket} does not exist!")

          datasetManager = DatasetObjectStore(bucket)
        } catch (e: Throwable) {
          log.error("failed to init dataset manager", e)
          abortCB(e.message)
        }

        try {
          kafkaRouter = KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
        } catch (e: Throwable) {
          log.error("failed to init kafka router", e)
          abortCB(e.message)
        }
      }
    }
  }

  suspend fun runFull(): Boolean {
    if (active.isLocked)
      return false

    val targets = AppDatabaseRegistry.asSequence()
      .map { (project, _) -> AppDBTarget(project, project) }
      .toMutableList<ReconcilerTarget>()
    targets.add(CacheDBTarget())

    val timer = Metrics.Reconciler.Full.reconcilerTimes.startTimer()

    log.info("running full reconciler for ${targets.size} targets")

    log.info("beginning reconciliation")
    coroutineScope {
      targets.forEach {
        launch {
          ReconcilerInstance(it, datasetManager, kafkaRouter, false, config.deletesEnabled).reconcile()
        }
      }
    }

    timer.observeDuration()

    return true
  }

  suspend fun runSlim(): Boolean {
    if (active.isLocked)
      return false

    val timer = Metrics.Reconciler.Slim.executionTime.startTimer()

    val target = CacheDBTarget()

    ReconcilerInstance(target, datasetManager, kafkaRouter, slim = true, deletesEnabled = false).reconcile()

    timer.observeDuration()

    return true
  }

  private fun workerName(tgt: ReconcilerTarget) = "recon-${tgt.name}"
}
