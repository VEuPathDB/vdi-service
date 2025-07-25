@file:Suppress("NOTHING_TO_INLINE")
package vdi.core.reconciler

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.model.data.DatasetType
import org.veupathdb.vdi.core.common.util.CloseableIterator
import java.time.OffsetDateTime
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.assertEquals
import vdi.core.db.model.ReconcilerTargetRecord
import vdi.core.kafka.router.KafkaRouter
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.core.s3.files.DatasetMetaFile

class ReconcilerTest {
  private val UpdateTime = OffsetDateTime.now()

  @Test
  @DisplayName("Test insert one, delete one at the end")
  fun test1() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()

    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, true)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(
        listOf(
          makeTargetRecord(111, "12345678123456781234567812345678"),
          // Missing 22345678123456781234567812345678, should be inserted in target.
          makeTargetRecord(111, "32345678123456781234567812345678"),
          makeTargetRecord(111, "42345678123456781234567812345678"),
        ).iterator()
      )
    )
    doReturn(listOf(
      mockDatasetDirectory(111L, "12345678123456781234567812345678"),
      mockDatasetDirectory(111L, "22345678123456781234567812345678"),
      mockDatasetDirectory(111L, "32345678123456781234567812345678"),
      // Missing 42345678123456781234567812345678, should be deleted in target.
    ).stream()).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val capturedDatasetID = mockingDetails(kafkaRouter).invocations
      .find { it.method.name == "sendReconciliationTrigger" }!!
      .getArgument<DatasetID>(1)
      .toString()
    assertEquals("Expected x but found y", "22345678123456781234567812345678", capturedDatasetID)
  }

  @Test
  @DisplayName("Test single dataset out of sync")
  fun test2() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, false)

    `when`(cacheDb.streamSortedSyncControlRecords())
      .thenReturn(closeableIterator(listOf(makeTargetRecord(111, "12345678123456781234567812345678")).iterator()))
    doReturn(listOf(
      mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime.plusDays(1)),
    ).stream()).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val capturedDatasetID = mockingDetails(kafkaRouter).invocations
      .find { it.method.name == "sendReconciliationTrigger" }!!
      .getArgument<DatasetID>(1)
      .toString()
    assertEquals("Expected x but found y", "12345678123456781234567812345678", capturedDatasetID)
  }

  @Test
  @DisplayName("Test single dataset in sync")
  fun test3() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, false)

    `when`(cacheDb.streamSortedSyncControlRecords())
      .thenReturn(closeableIterator(listOf(makeTargetRecord(111, "12345678123456781234567812345678")).iterator()))
    doReturn(listOf(
      mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime),
    ).stream()).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    assertEquals(0, mockingDetails(kafkaRouter).invocations.size)
  }

  @Test
  @DisplayName("Test target DB missing all datasets")
  fun test4() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, false)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(emptyList<ReconcilerTargetRecord>().iterator())
    )
    doReturn(
      listOf(
        mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime),
        mockDatasetDirectory(111L, "22345678123456781234567812345678", UpdateTime),
        mockDatasetDirectory(111L, "32345678123456781234567812345678", UpdateTime),
      ).stream()
    ).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    assertEquals(3, mockingDetails(kafkaRouter).invocations.size)
  }

  @Test
  @DisplayName("Test delete dataset in middle of stream")
  fun test5() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, true)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(listOf(
        makeTargetRecord(111, "12345678123456781234567812345678"),
        makeTargetRecord(111, "22345678123456781234567812345678"),
        makeTargetRecord(111, "32345678123456781234567812345678"),
        makeTargetRecord(111, "42345678123456781234567812345678"),
      ).iterator())
    )
    doReturn(listOf(
      mockDatasetDirectory(111L, "12345678123456781234567812345678"),
      // Missing 22345678123456781234567812345678, should be deleted in target.
      mockDatasetDirectory(111L, "32345678123456781234567812345678"),
      mockDatasetDirectory(111L, "42345678123456781234567812345678"),
    ).stream()).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val capturedDatasetID = mockingDetails(cacheDb).invocations
      .find { it.method.name == "deleteDataset" }!!
      .getArgument<ReconcilerTargetRecord>(0)
      .datasetID
    assertEquals("test", "22345678123456781234567812345678", capturedDatasetID.toString())
  }

  // FIXME: disabled, to be removed when deletes are moved to the hard-delete
  //        lane.
  // @Test
  @DisplayName("Test delete last datasets in target stream, then sync last source")
  fun test6() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, true)

    `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(listOf(
        makeTargetRecord(111, "22345678123456781234567812345678"),
        makeTargetRecord(111, "32345678123456781234567812345678"),
        makeTargetRecord(111, "42345678123456781234567812345678"),
      ).iterator())
    )
    doReturn(listOf(
      mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime.plusDays(1)),
      mockDatasetDirectory(111L, "52345678123456781234567812345678", UpdateTime.plusDays(1)),
    ).stream()).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val syncedIDs = mockingDetails(kafkaRouter).invocations
      .filter { it.method.name == "sendReconciliationTrigger" }
      .map { it.getArgument<DatasetID>(1).toString() }
    val deletedIDs = mockingDetails(cacheDb).invocations
      .filter { it.method.name == "deleteDataset" }
      .map { it.getArgument<DatasetID>(1).toString() }

    assertEquals(listOf("12345678123456781234567812345678", "52345678123456781234567812345678"), syncedIDs)
    assertEquals(listOf("22345678123456781234567812345678", "32345678123456781234567812345678", "42345678123456781234567812345678"), deletedIDs)
  }

  @Test
  @DisplayName("Test different owners")
  fun test7() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, false)

    `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(listOf(
        makeTargetRecord(111, "bbb"),
        makeTargetRecord(333, "aaa"),
      ).iterator())
    )
    doReturn(
      listOf(
        mockDatasetDirectory(111L, "bbb", UpdateTime),
        mockDatasetDirectory(222L, "zzz", UpdateTime),
        mockDatasetDirectory(333L, "aaa", UpdateTime),
      ).stream()
    ).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val deletedIDs = mockingDetails(cacheDb).invocations
      .filter { it.method.name == "deleteDataset" }
      .map { it.getArgument<DatasetID>(1).toString() }

    assertEquals(listOf(), deletedIDs)
  }


  @DisplayName("Test case sensitivity")
  fun test8() {
    val cacheDb = mock<ReconcilerTarget>()
    `when`(cacheDb.name).thenReturn("CacheDB")
    val datasetManager = mock<DatasetObjectStore>()
    val kafkaRouter = mock<KafkaRouter>()
    val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false, false)

    `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

    `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
      closeableIterator(listOf(
        makeTargetRecord(111, "Vbz2OgjnKsR"),
      ).iterator())
    )
    doReturn(
      listOf(
        mockDatasetDirectory(111L, "eA1WkZhiGbE", UpdateTime.plusDays(1)),
        mockDatasetDirectory(111L, "Vbz2OgjnKsR", UpdateTime.plusDays(1)),
      ).stream()
    ).`when`(datasetManager).streamAllDatasets()
    runBlocking { recon.reconcile() }
    val deletedIDs = mockingDetails(cacheDb).invocations
      .filter { it.method.name == "deleteDataset" }
      .map { it.getArgument<DatasetID>(1).toString() }

    assertEquals(listOf(), deletedIDs)
  }

  private fun closeableIterator(iterator: Iterator<ReconcilerTargetRecord>): CloseableIterator<ReconcilerTargetRecord> {
    return object: CloseableIterator<ReconcilerTargetRecord> {
      override fun close() {
        // do nothing
      }

      override fun hasNext(): Boolean {
        return iterator.hasNext()
      }

      override fun next(): ReconcilerTargetRecord {
        return iterator.next()
      }
    }
  }

  private fun mockDatasetDirectory(userID: Long, datasetID: String): DatasetDirectory {
    val dsMock = mock<DatasetDirectory>()
    `when`(dsMock.datasetID).thenReturn(DatasetID(datasetID))
    `when`(dsMock.ownerID).thenReturn(UserID(userID))
    `when`(dsMock.getInstallReadyTimestamp() ?: UpdateTime).thenReturn(UpdateTime)
    `when`(dsMock.getLatestShareTimestamp(UpdateTime)).thenReturn(UpdateTime)
    val meta = mock<DatasetMetaFile>()
    `when`(meta.lastModified()).thenReturn(UpdateTime)
    `when`(dsMock.getMetaFile()).thenReturn(meta)
    return dsMock
  }

  private fun mockDatasetDirectory(userID: Long, datasetID: String, syncTime: OffsetDateTime): DatasetDirectory {
    val dsMock = mock<DatasetDirectory>()
    `when`(dsMock.datasetID).thenReturn(DatasetID(datasetID))
    `when`(dsMock.ownerID).thenReturn(UserID(userID))
    `when`(dsMock.getInstallReadyTimestamp() ?: UpdateTime).thenReturn(syncTime)
    `when`(dsMock.getLatestShareTimestamp(UpdateTime)).thenReturn(syncTime)
    val meta = mock<DatasetMetaFile>()
    `when`(meta.lastModified()).thenReturn(syncTime)
    `when`(dsMock.getMetaFile()).thenReturn(meta)
    return dsMock
  }

  private inline fun makeTargetRecord(
    ownerID: Long,
    datasetID: String,
    sharesUpdated: OffsetDateTime = UpdateTime,
    dataUpdated: OffsetDateTime = UpdateTime,
    metaUpdated: OffsetDateTime = UpdateTime,
    type: DatasetType = DatasetType(DataType.of("Stub"), "Stub"),
    isUninstalled: Boolean = false,
  ) =
    ReconcilerTargetRecord(
      ownerID = UserID(ownerID),
      datasetID = DatasetID(datasetID),
      sharesUpdated = sharesUpdated,
      dataUpdated = dataUpdated,
      metaUpdated = metaUpdated,
      type = type,
      isUninstalled = isUninstalled,
    )

}
