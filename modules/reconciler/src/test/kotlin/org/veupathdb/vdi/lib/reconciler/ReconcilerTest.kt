package org.veupathdb.vdi.lib.reconciler

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetTypeImpl
import org.veupathdb.vdi.lib.common.model.VDIReconcilerTargetRecord
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.CloseableIterator
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.DatasetMetaFile
import java.time.OffsetDateTime
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.assertEquals

class ReconcilerTest {
    private val UpdateTime = OffsetDateTime.now()

    @Test
    @DisplayName("Test insert one, delete one at the end")
    fun test1() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()

        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(
                listOf(
                    VDIReconcilerTargetRecord(
                        type = VDIDatasetTypeImpl("Stub", "Stub"),
                        owner = UserID("111"),
                        syncControlRecord = VDISyncControlRecord(
                            datasetID = DatasetID("12345678123456781234567812345678"),
                            sharesUpdated = UpdateTime,
                            dataUpdated = UpdateTime,
                            metaUpdated = UpdateTime
                        )
                    ),
                    // Missing 22345678123456781234567812345678, should be inserted in target.
                    VDIReconcilerTargetRecord(
                        type = VDIDatasetTypeImpl("Stub", "Stub"),
                        owner = UserID("111"),
                        syncControlRecord = VDISyncControlRecord(
                            datasetID = DatasetID("32345678123456781234567812345678"),
                            sharesUpdated = UpdateTime,
                            dataUpdated = UpdateTime,
                            metaUpdated = UpdateTime
                        )
                    ),
                    VDIReconcilerTargetRecord(
                        type = VDIDatasetTypeImpl("Stub", "Stub"),
                        owner = UserID("111"),
                syncControlRecord = VDISyncControlRecord(
                            datasetID = DatasetID("42345678123456781234567812345678"),
                            sharesUpdated = UpdateTime,
                            dataUpdated = UpdateTime,
                            metaUpdated = UpdateTime
                        )
                    )
                ).iterator()
            )
        )
        doReturn(listOf(
            mockDatasetDirectory(111L, "12345678123456781234567812345678"),
            mockDatasetDirectory(111L, "22345678123456781234567812345678"),
            mockDatasetDirectory(111L, "32345678123456781234567812345678"),
            // Missing 42345678123456781234567812345678, should be deleted in target.
        ).stream()).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val capturedDatasetID = mockingDetails(kafkaRouter).invocations
            .find { it.method.name == "sendUpdateMetaTrigger" }!!
            .getArgument<UpdateMetaTrigger>(0)
            .datasetID.toString()
        assertEquals("Expected x but found y", "22345678123456781234567812345678", capturedDatasetID)
    }

    @Test
    @DisplayName("Test single dataset out of sync")
    fun test2() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(
                listOf(
                    VDIReconcilerTargetRecord(
                        type = VDIDatasetTypeImpl("Stub", "Stub"),
                        owner = UserID("111"),
                        syncControlRecord = VDISyncControlRecord(
                            datasetID = DatasetID("12345678123456781234567812345678"),
                            sharesUpdated = UpdateTime,
                            dataUpdated = UpdateTime,
                            metaUpdated = UpdateTime
                        )
                    ),
                ).iterator()
            )
        )
        doReturn(listOf(
            mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime.plusDays(1)),
        ).stream()).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val capturedDatasetID = mockingDetails(kafkaRouter).invocations
            .find { it.method.name == "sendUpdateMetaTrigger" }!!
            .getArgument<UpdateMetaTrigger>(0)
            .datasetID.toString()
        assertEquals("Expected x but found y", "12345678123456781234567812345678", capturedDatasetID)
    }

    @Test
    @DisplayName("Test single dataset in sync")
    fun test3() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(
                listOf(
                    VDIReconcilerTargetRecord(
                        type = VDIDatasetTypeImpl("Stub", "Stub"),
                        owner = UserID("111"),
                        syncControlRecord =  VDISyncControlRecord(
                            datasetID = DatasetID("12345678123456781234567812345678"),
                            sharesUpdated = UpdateTime,
                            dataUpdated = UpdateTime,
                            metaUpdated = UpdateTime
                        )
                    ),
                ).iterator()
            )
        )
        doReturn(listOf(
            mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime),
        ).stream()).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        assertEquals(0, mockingDetails(kafkaRouter).invocations.size)
    }

    @Test
    @DisplayName("Test target DB missing all datasets")
    fun test4() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(emptyList<VDIReconcilerTargetRecord>().iterator())
        )
        doReturn(
            listOf(
                mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime),
                mockDatasetDirectory(111L, "22345678123456781234567812345678", UpdateTime),
                mockDatasetDirectory(111L, "32345678123456781234567812345678", UpdateTime),
            ).stream()
        ).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        assertEquals(3, mockingDetails(kafkaRouter).invocations.size)
    }

    @Test
    @DisplayName("Test delete dataset in middle of stream")
    fun test5() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(listOf(
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("12345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("22345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("32345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("42345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                )
            ).iterator())
        )
        doReturn(listOf(
            mockDatasetDirectory(111L, "12345678123456781234567812345678"),
            // Missing 22345678123456781234567812345678, should be deleted in target.
            mockDatasetDirectory(111L, "32345678123456781234567812345678"),
            mockDatasetDirectory(111L, "42345678123456781234567812345678"),
        ).stream()).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val capturedDatasetID = mockingDetails(cacheDb).invocations
            .find { it.method.name == "deleteDataset" }!!
            .getArgument<DatasetID>(1)
        assertEquals("test", "22345678123456781234567812345678", capturedDatasetID.toString())
    }

    @Test
    @DisplayName("Test delete last datasets in target stream, then sync last source")
    fun test6() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(listOf(
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("22345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("32345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("42345678123456781234567812345678"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                ).iterator())
        )
        doReturn(listOf(
            mockDatasetDirectory(111L, "12345678123456781234567812345678", UpdateTime.plusDays(1)),
            mockDatasetDirectory(111L, "52345678123456781234567812345678", UpdateTime.plusDays(1)),
            ).stream()).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val syncedIDs = mockingDetails(kafkaRouter).invocations
            .filter { it.method.name == "sendUpdateMetaTrigger" }
            .map { it.getArgument<UpdateMetaTrigger>(0).datasetID.toString() }
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
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter, false)

        `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(listOf(
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("bbb"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("333"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("aaa"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                )
            ).iterator())
        )
        doReturn(
            listOf(
                mockDatasetDirectory(111L, "bbb", UpdateTime),
                mockDatasetDirectory(222L, "zzz", UpdateTime),
                mockDatasetDirectory(333L, "aaa", UpdateTime),
                ).stream()
        ).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val deletedIDs = mockingDetails(cacheDb).invocations
            .filter { it.method.name == "deleteDataset" }
            .map { it.getArgument<DatasetID>(1).toString() }

        assertEquals(listOf(), deletedIDs)
    }


    @DisplayName("Test case sensitivity")
    fun test8() {
        val cacheDb = mock<ReconcilerTarget>()
        `when`(cacheDb.name).thenReturn("CacheDB")
        val datasetManager = mock<DatasetManager>()
        val kafkaRouter = mock<KafkaRouter>()
        val recon = ReconcilerInstance(cacheDb, datasetManager, kafkaRouter)

        `when`(cacheDb.type).thenReturn(ReconcilerTargetType.Cache)

        `when`(cacheDb.streamSortedSyncControlRecords()).thenReturn(
            closeableIterator(listOf(
                VDIReconcilerTargetRecord(
                    type = VDIDatasetTypeImpl("Stub", "Stub"),
                    owner = UserID("111"),
                    syncControlRecord = VDISyncControlRecord(
                        datasetID = DatasetID("Vbz2OgjnKsR"),
                        sharesUpdated = UpdateTime,
                        dataUpdated = UpdateTime,
                        metaUpdated = UpdateTime
                    )
                ),
            ).iterator())
        )
        doReturn(
            listOf(
                mockDatasetDirectory(111L, "eA1WkZhiGbE", UpdateTime.plusDays(1)),
                mockDatasetDirectory(111L, "Vbz2OgjnKsR", UpdateTime.plusDays(1)),
            ).stream()
        ).`when`(datasetManager).streamAllDatasets()
        recon.reconcile()
        val deletedIDs = mockingDetails(cacheDb).invocations
            .filter { it.method.name == "deleteDataset" }
            .map { it.getArgument<DatasetID>(1).toString() }

        assertEquals(listOf(), deletedIDs)
    }

    private fun closeableIterator(iterator: Iterator<VDIReconcilerTargetRecord>): CloseableIterator<VDIReconcilerTargetRecord> {
        return object: CloseableIterator<VDIReconcilerTargetRecord> {
            override fun close() {
                // do nothing
            }

            override fun hasNext(): Boolean {
                return iterator.hasNext()
            }

            override fun next(): VDIReconcilerTargetRecord {
                return iterator.next()
            }
        }
    }

    private fun mockDatasetDirectory(userID: Long, datasetID: String): DatasetDirectory {
        val dsMock = mock<DatasetDirectory>()
        `when`(dsMock.datasetID).thenReturn(DatasetID(datasetID))
        `when`(dsMock.ownerID).thenReturn(UserID(userID))
        `when`(dsMock.getLatestDataTimestamp(UpdateTime)).thenReturn(UpdateTime)
        `when`(dsMock.getLatestShareTimestamp(UpdateTime)).thenReturn(UpdateTime)
        val meta = mock<DatasetMetaFile>()
        `when`(meta.lastModified()).thenReturn(UpdateTime)
        `when`(dsMock.getMeta()).thenReturn(meta)
        return dsMock
    }

    private fun mockDatasetDirectory(userID: Long, datasetID: String, syncTime: OffsetDateTime): DatasetDirectory {
        val dsMock = mock<DatasetDirectory>()
        `when`(dsMock.datasetID).thenReturn(DatasetID(datasetID))
        `when`(dsMock.ownerID).thenReturn(UserID(userID))
        `when`(dsMock.getLatestDataTimestamp(UpdateTime)).thenReturn(syncTime)
        `when`(dsMock.getLatestShareTimestamp(UpdateTime)).thenReturn(syncTime)
        val meta = mock<DatasetMetaFile>()
        `when`(meta.lastModified()).thenReturn(syncTime)
        `when`(dsMock.getMeta()).thenReturn(meta)
        return dsMock
    }

}