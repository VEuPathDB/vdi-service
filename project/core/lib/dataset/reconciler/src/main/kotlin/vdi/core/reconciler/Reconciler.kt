package vdi.core.reconciler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.veupathdb.lib.s3.s34k.S3Api
import kotlin.time.Duration.Companion.seconds
import vdi.core.db.app.AppDatabaseRegistry
import vdi.core.kafka.router.KafkaRouter
import vdi.core.kafka.router.KafkaRouterFactory
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.s3.DatasetObjectStore

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

    log.info("starting full reconciliation for ${targets.size} targets")
    coroutineScope {
      targets.forEach {
        launch {
          ReconcilerInstance(it, datasetManager, kafkaRouter, false, config.deletesEnabled).reconcile()
        }
      }
    }

    val time = timer.observeDuration()
    log.info("full reconciliation completed in {}", time.seconds)

    return true
  }

  suspend fun runSlim(): Boolean {
    if (active.isLocked)
      return false

    log.info("starting slim reconciliation")

    val timer = Metrics.Reconciler.Slim.executionTime.startTimer()
    val target = CacheDBTarget()

    ReconcilerInstance(target, datasetManager, kafkaRouter, slim = true, deletesEnabled = false).reconcile()

    val time = timer.observeDuration()
    log.info("slim reconciliation completed in {}", time.seconds)

    return true
  }
}
