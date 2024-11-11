package vdi.lane.reconciliation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.*
import vdi.component.db.app.model.DeleteFlag
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.kafka.EventSource
import vdi.test.*
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

private val userID = UserID(123456)

private val datasetID = DatasetID()

@DisplayName("when dataset")
class DatasetReconcilerTest {

  private val dsType = VDIDatasetTypeImpl(DataType.of("foo"), "bar")

  private val projects = setOf("foo")

  private fun generalMetaMock() =
    mockDatasetMeta(
      type = dsType,
      projects = projects,
      visibility = VDIDatasetVisibility.Protected,
      owner = userID,
      name = "fizz",
      summary = "buzz",
      description = "something",
      origin = "origin",
      sourceURL = "source url",
      dependencies = emptyList(),
      created = OriginTimestamp,
    )

  @Nested
  @DisplayName("has no meta file in the data store")
  inner class NoMetaFile {

    @Test
    @DisplayName("then reconciliation does nothing")
    fun test0() {
      // test that the process did not proceed past the meta load call by making
      // sure the [tryInitCacheDB] method is not called.  The first thing that
      // method does is call out to the cache db, so if the cache db is not used
      // then we didn't get there.
      val dsMan = mockDatasetManager()
      val cacheDB = mockCacheDB()
      val appDB = mockAppDB()
      val router = mockKafkaRouter()

      DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

      verifyNoInteractions(cacheDB)
      verifyNoInteractions(appDB)
      verifyNoInteractions(router)
    }
  }

  @Nested
  @DisplayName("has no dataset record in the cache db")
  inner class NoCacheRecord {

    @Test
    @DisplayName("then reconciliation inserts a dataset record")
    fun test0() {

      val inputFiles = listOf(generateFileInfo())
      val outputFiles = listOf(generateFileInfo())

      val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
        mockDatasetDirectory(
          ownerID = userID,
          datasetID = datasetID,
          metaFile = mockMetaFile(meta = generalMetaMock()),
          manifest = mockManifestFile(manifest = mockDatasetManifest(inputFiles = inputFiles, dataFiles = outputFiles)),
        )
      })

      val transaction = mockCacheDBTransaction(
        onInsertDataset = {
          assertEquals(datasetID, it.datasetID)
          assertEquals(dsType.name, it.typeName)
          assertEquals(dsType.version, it.typeVersion)
          assertEquals(userID, it.ownerID)
          assertFalse(it.isDeleted) // This should be false because we didn't mock a delete flag
          assertEquals(OriginTimestamp, it.created)
          // We don't care about import status as it isn't used in the insert
          assertEquals("origin", it.origin)
          // We can't really check the inserted timestamp reliably
        },
        onInsertMeta = {
          assertEquals(datasetID, it.datasetID)
          assertEquals(VDIDatasetVisibility.Protected, it.visibility)
          assertEquals("fizz", it.name)
          assertEquals("buzz", it.summary)
          assertEquals("something", it.description)
          assertEquals("source url", it.sourceURL)
        },
        onInsertSyncControl = {
          assertEquals(datasetID, it.datasetID)
          assertEquals(OriginTimestamp, it.sharesUpdated)
          assertEquals(OriginTimestamp, it.dataUpdated)
          assertEquals(OriginTimestamp, it.metaUpdated)
        },
        onClose = { throw Exception("throwing to end dataset reconciliation process") }
      )
      val cacheDB = mockCacheDB(onOpenTransaction = { transaction })

      val appDB = mockAppDB()

      val router = mockKafkaRouter()

      DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

      verify(cacheDB, times(1)).openTransaction()
      verifyNoMoreInteractions(cacheDB)

      verify(transaction, times(1)).tryInsertDataset(any())
      verify(transaction, times(1)).tryInsertDatasetMeta(any())
      verify(transaction, times(1)).tryInsertDatasetProjects(datasetID, projects)
      // Because we didn't mock an import-ready file or an install-ready file,
      // the process should attempt to set the status to failed for invalid
      // state.
      verify(transaction, times(1)).tryInsertImportControl(datasetID, DatasetImportStatus.Failed)
      verify(transaction, times(1)).tryInsertImportMessages(eq(datasetID), any())
      verify(transaction, times(1)).tryInsertSyncControl(any())
      verify(transaction, times(1)).tryInsertUploadFiles(datasetID, inputFiles)
      verify(transaction, times(1)).tryInsertInstallFiles(datasetID, outputFiles)
      verify(transaction, times(1)).commit()
      verify(transaction, times(1)).close()
      verifyNoMoreInteractions(transaction)

      verifyNoInteractions(appDB)
      verifyNoInteractions(router)
    }
  }

  @Nested
  @DisplayName("has a delete flag in the data store")
  inner class HasDeleteFlag {

    @Nested
    @DisplayName("and does not exist in the cache db")
    inner class NotInCacheDB {
      @Test
      @DisplayName("then reconciliation inserts a deleted dataset into the cache db")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val transaction = mockCacheDBTransaction(
          onInsertDataset = { assertTrue(it.isDeleted) },
          onClose = { throw Exception("throwing to end dataset reconciliation process") }
        )
        val cacheDB = mockCacheDB(onOpenTransaction = { transaction })

        val appDB = mockAppDB()

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(transaction, times(1)).tryInsertDataset(any())

        // Because we throw an exception when closing the cache-db transaction,
        // nothing else should happen
        verifyNoInteractions(appDB)
        verifyNoInteractions(router)
      }
    }

    @Nested
    @DisplayName("and exists in the cache db")
    inner class InCacheDB {

      @Test
      @DisplayName("then reconciliation updates the deleted flag")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val transaction = mockCacheDBTransaction(
          onUpdateDatasetDeleted = { _, _ -> throw Exception("throwing to end dataset reconciliation process") }
        )
        val cacheDB = mockCacheDB(
          onSelectDataset = { mockCacheDatasetRecord(isDeleted = false) },
          onOpenTransaction = { transaction }
        )

        val appDB = mockAppDB()

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(transaction, times(1)).updateDatasetDeleted(datasetID, true)

        verifyNoInteractions(appDB)
        verifyNoInteractions(router)
      }
    }

    @Nested
    @DisplayName("and is uninstalled from all targets")
    inner class IsUninstalled {

      @Test
      @DisplayName("then no action should be taken")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val cacheDB = mockCacheDB(onSelectDataset = { mockCacheDatasetRecord(isDeleted = true) })

        val accessor = mockAppDBAccessor(
          dataset = { mockAppDatasetRecord(isDeleted = DeleteFlag.DeletedAndUninstalled) }
        )

        val appDB = mockAppDB(accessor = { _, _ -> accessor })

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(appDB, times(1)).accessor(projects.first(), dsType.name)
        verify(accessor, times(1)).selectDataset(datasetID)

        verifyNoInteractions(router)
      }
    }

    @Nested
    @DisplayName("and is not uninstalled from all targets")
    inner class IsNotUninstalled {

      @Test
      @DisplayName("then an uninstall event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val cacheDB = mockCacheDB(onSelectDataset = { mockCacheDatasetRecord(isDeleted = true) })

        val accessor = mockAppDBAccessor(
          dataset = { mockAppDatasetRecord(isDeleted = DeleteFlag.DeletedNotUninstalled) }
        )

        val appDB = mockAppDB(accessor = { _, _ -> accessor })

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(appDB, times(1)).accessor(projects.first(), dsType.name)
        verify(accessor, times(1)).selectDataset(datasetID)

        verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("and install target is disabled")
    inner class TargetIsDisabled {

      @Test
      @DisplayName("then an uninstall event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val cacheDB = mockCacheDB(onSelectDataset = { mockCacheDatasetRecord(isDeleted = true) })
        val appDB = mockAppDB()
        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(appDB, times(1)).accessor(projects.first(), dsType.name)

        verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("and is not present in install target")
    inner class NotInTarget {

      @Test
      @DisplayName("then an uninstall event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasDeleteFlag = true,
          )
        })

        val cacheDB = mockCacheDB(onSelectDataset = { mockCacheDatasetRecord(isDeleted = true) })

        val accessor = mockAppDBAccessor()

        val appDB = mockAppDB(accessor = { _, _ -> accessor })
        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(appDB, times(1)).accessor(projects.first(), dsType.name)
        verify(accessor, times(1)).selectDataset(datasetID)

        verify(router, times(1)).sendSoftDeleteTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }
  }

  @Nested
  @DisplayName("has import-ready file in the data store")
  inner class HasImportReady {

    @Nested
    @DisplayName("and has already failed import")
    inner class FailedImport {

      @Nested
      @DisplayName("but has no import messages")
      inner class NoMessages {

        @Test
        @DisplayName("then an import event should be fired")
        fun test0() {
          val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
            mockDatasetDirectory(
              ownerID = userID,
              datasetID = datasetID,
              metaFile = mockMetaFile(meta = generalMetaMock()),
              hasImportableFile = true,
            )
          })

          val transaction = mockCacheDBTransaction()

          val cacheDB = mockCacheDB(
            onSelectImportControl = { DatasetImportStatus.Failed },
            onSelectImportMessages = { emptyList() },
            onOpenTransaction = { transaction }
          )

          val router = mockKafkaRouter(
            onSendImport = { _, _, _ -> throw Exception("exception to halt dataset reconciler") }
          )

          DatasetReconciler(cacheDB, mockAppDB(), router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

          verify(cacheDB, times(1)).selectImportControl(datasetID)
          verify(cacheDB, times(1)).selectImportMessages(datasetID)
          verify(transaction, times(1)).upsertImportControl(datasetID, DatasetImportStatus.Queued)
          verify(router, times(1)).sendImportTrigger(userID, datasetID, EventSource.SlimReconciler)
          verifyNoMoreInteractions(router)
        }
      }

      @Nested
      @DisplayName("and has import messages")
      inner class HasMessages {

        @Test
        @DisplayName("then no import event should be fired")
        fun test0() {
          val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
            mockDatasetDirectory(
              ownerID = userID,
              datasetID = datasetID,
              metaFile = mockMetaFile(meta = generalMetaMock()),
              hasImportableFile = true,
            )
          })

          val cacheDB = mockCacheDB(
            onSelectImportControl = { DatasetImportStatus.Failed },
            onSelectImportMessages = { listOf("beep") },
          )

          val appDB = mockAppDB()

          val router = mockKafkaRouter()

          DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

          // Called in tryInitCacheDB and main reconciliation path
          verify(cacheDB, times(1)).selectImportControl(datasetID)
          verify(cacheDB, times(1)).selectImportMessages(datasetID)

          verifyNoInteractions(appDB)
          verifyNoInteractions(router)
        }
      }
    }

    @Nested
    @DisplayName("and has an install-ready file in the data store")
    inner class InstallReady {

      @Nested
      @DisplayName("but has no manifest file")
      inner class NoManifest {

        @Test
        @DisplayName("then an import event should be fired")
        fun test0() {
          val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
            mockDatasetDirectory(
              ownerID = userID,
              datasetID = datasetID,
              metaFile = mockMetaFile(meta = generalMetaMock()),
              hasImportableFile = true,
              hasInstallableFile = true,
            )
          })

          val transaction = mockCacheDBTransaction()
          val cacheDB = mockCacheDB(onOpenTransaction = { transaction })
          val appDB = mockAppDB()
          val router = mockKafkaRouter()

          DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

          verify(transaction, times(1)).upsertImportControl(datasetID, DatasetImportStatus.Queued)

          verifyNoInteractions(appDB)

          verify(router, times(1)).sendImportTrigger(userID, datasetID, EventSource.SlimReconciler)
          verifyNoMoreInteractions(router)
        }
      }

      @Nested
      @DisplayName("and has a manifest file")
      inner class WithManifest {

        @Test
        @DisplayName("then no import event should be fired")
        fun test0() {
          val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
            mockDatasetDirectory(
              ownerID = userID,
              datasetID = datasetID,
              metaFile = mockMetaFile(meta = generalMetaMock()),
              hasImportableFile = true,
              hasInstallableFile = true,
              manifest = mockManifestFile(manifest = mockDatasetManifest()),
            )
          })

          val cacheDB = mockCacheDB()
          val appDB = mockAppDB()
          val router = mockKafkaRouter()

          DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

          verifyNoInteractions(appDB)
          verifyNoInteractions(router)
        }
      }
    }
  }

  @Nested
  @DisplayName("has no import-ready file in the data store")
  inner class NoImportReady {

    @Nested
    @DisplayName("but has install-ready file and manifest")
    inner class IsImported {

      @Test
      @DisplayName("then the dataset import status should be set to complete in the cache db")
      fun test0() {
        val dsMan = mockDatasetManager(onGetDatasetDirectory = { userID, datasetID ->
          mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(meta = generalMetaMock()),
            hasInstallableFile = true,
            manifest = mockManifestFile(manifest = mockDatasetManifest()),
          )
        })

        val transaction = mockCacheDBTransaction(
          onClose = { throw Exception("exception to halt dataset reconciler") }
        )
        val cacheDB = mockCacheDB(onOpenTransaction = { transaction })
        val appDB = mockAppDB()
        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(transaction, times(1)).tryInsertImportControl(datasetID, DatasetImportStatus.Complete)

        verifyNoInteractions(appDB)
        verifyNoInteractions(router)
      }
    }
  }

  @Nested
  @DisplayName("cache db records are out of sync")
  inner class CacheDBSync {

    @Nested
    @DisplayName("due to out of date metadata")
    inner class BadMeta {

      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = OffsetDateTime.now(),
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest())
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        )

        val appDB = mockAppDB()

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendUpdateMetaTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("due to out of date shares")
    inner class BadShares {

      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = OriginTimestamp,
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest()),
            onGetLatestShareTimestamp = { OffsetDateTime.now() }
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        )

        val appDB = mockAppDB()

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendShareTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("due to out of date install data")
    inner class BadData {


      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = OriginTimestamp,
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = OffsetDateTime.now(),
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest()),
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        )

        val appDB = mockAppDB()

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendInstallTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }
  }

  @Nested
  @DisplayName("app db records are out of sync")
  inner class AppDBSync {

    @Nested
    @DisplayName("due to out of date metadata")
    inner class BadMeta {

      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val now = OffsetDateTime.now()

        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = now,
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest())
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, now) }
        )

        val appDB = mockAppDB(accessor = { _, _ -> mockAppDBAccessor(
          syncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        ) })

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendUpdateMetaTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("due to out of date shares")
    inner class BadShares {

      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val now = OffsetDateTime.now()

        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = OriginTimestamp,
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest()),
            onGetLatestShareTimestamp = { now }
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, now, OriginTimestamp, OriginTimestamp) }
        )

        val appDB = mockAppDB(accessor = { _, _ -> mockAppDBAccessor(
          syncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        ) })

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendShareTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }

    @Nested
    @DisplayName("due to out of date install data")
    inner class BadData {


      @Test
      @DisplayName("then an update-meta event should be fired")
      fun test0() {
        val now = OffsetDateTime.now()

        val dsMan = mockDatasetManager(
          onGetDatasetDirectory = { _, _ -> mockDatasetDirectory(
            ownerID = userID,
            datasetID = datasetID,
            metaFile = mockMetaFile(
              meta = generalMetaMock(),
              lastModified = OriginTimestamp,
            ),
            importableFile = mockImportableFile(
              exists = true,
              lastModified = OriginTimestamp,
            ),
            installableFile = mockInstallableFile(
              exists = true,
              lastModified = now,
            ),
            manifest = mockManifestFile(manifest = mockDatasetManifest()),
          ) }
        )

        val cacheDB = mockCacheDB(
          onSelectSyncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, now, OriginTimestamp) }
        )

        val appDB = mockAppDB(accessor = { _, _ -> mockAppDBAccessor(
          syncControl = { VDISyncControlRecord(datasetID, OriginTimestamp, OriginTimestamp, OriginTimestamp) }
        ) })

        val router = mockKafkaRouter()

        DatasetReconciler(cacheDB, appDB, router, dsMan).reconcile(userID, datasetID, EventSource.SlimReconciler)

        verify(router, times(1)).sendInstallTrigger(userID, datasetID, EventSource.SlimReconciler)
        verifyNoMoreInteractions(router)
      }
    }
  }
}

private fun generateFileInfo(): VDIDatasetFileInfo =
  VDIDatasetFileInfoImpl(
    filename = UUID.randomUUID().toString(),
    fileSize = Random.nextLong(),
  )
