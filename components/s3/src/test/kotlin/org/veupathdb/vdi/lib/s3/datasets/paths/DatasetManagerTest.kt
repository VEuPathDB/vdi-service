package org.veupathdb.vdi.lib.s3.datasets.paths

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.core.fields.BasicHeaders
import org.veupathdb.lib.s3.s34k.core.objects.AbstractS3Object
import org.veupathdb.lib.s3.s34k.fields.Headers
import org.veupathdb.lib.s3.s34k.objects.*
import org.veupathdb.lib.s3.s34k.params.DeleteParams
import org.veupathdb.lib.s3.s34k.params.`object`.ObjectExistsParams
import org.veupathdb.lib.s3.s34k.params.`object`.ObjectStatParams
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.time.OffsetDateTime

class DatasetManagerTest {
    private val TestUserID1 = UserID("111")
    private val TestUserID2 = UserID("222")

    private val DatasetID1 = DatasetID("abcdabcdabcdabcd1111222233334444")
    private val DatasetID2 = DatasetID("aaaaaaaaaaaaaaaa1111222233334444")
    private val DatasetID3 = DatasetID("aaaaaaaaaaaaaaaa1111222233335555")

    @Test
    @DisplayName("Test two identical datasets with meta/manifest")
    fun test1() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)

        val mockedS3 = mockS3List(listOf(
            firstPathFactory.datasetMetaFile(),
            firstPathFactory.datasetManifestFile(),
            secondPathFactory.datasetMetaFile(),
            secondPathFactory.datasetManifestFile()
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(2))
        datasets.forEach {
            assertTrue(it.getManifest().exists())
            assertTrue(it.getMeta().exists())
            assertFalse(it.hasDeleteFlag())
            assertThat(it.getDataFiles(), Matchers.empty())
        }
    }

    @Test
    @DisplayName("Test two datasets, last with single file")
    fun test2() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)

        val mockedS3 = mockS3List(listOf(
            firstPathFactory.datasetMetaFile(),
            firstPathFactory.datasetManifestFile(),
            secondPathFactory.datasetMetaFile(),
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(2))
        datasets.forEach {
            assertTrue(it.getMeta().exists())
        }
        assertFalse(datasets.last().hasManifest())
    }

    @Test
    @DisplayName("Test datasets different users, heterogeneous files")
    fun test3() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)
        val thirdPathFactory = S3DatasetPathFactory(TestUserID2, DatasetID3)

        val mockedS3 = mockS3List(listOf(
            firstPathFactory.datasetMetaFile(),
            firstPathFactory.datasetManifestFile(),
            secondPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetShareOfferFile(TestUserID1),
            thirdPathFactory.datasetShareReceiptFile(TestUserID1)
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(3))
        assertThat(datasets.last().getShares().entries, Matchers.hasSize(1))
    }

    @Test
    @DisplayName("Test dataset with a delete flag.")
    fun test4() {
        val pathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)

        val mockedS3 = mockS3List(
            listOf(
                pathFactory.datasetMetaFile(),
                pathFactory.datasetManifestFile(),
                pathFactory.datasetDeleteFlagFile()
            )
        )
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(1))
    }

    @Test
    @DisplayName("Test dataset with garbage.")
    fun test5() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)
        val thirdPathFactory = S3DatasetPathFactory(TestUserID2, DatasetID3)

        val mockedS3 = mockS3List(listOf(
            // First dataset has just meta
            firstPathFactory.datasetMetaFile(),

            // Random garbage path should be skipped.
            "$TestUserID1/Broken-path",

            // Second dataset with just meta
            secondPathFactory.datasetMetaFile(),

            // Third dataset with meta and shares.
            thirdPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetShareOfferFile(TestUserID1),
            thirdPathFactory.datasetShareReceiptFile(TestUserID1)
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(3))
        assertThat(datasets.last().getShares().entries, Matchers.hasSize(1))
    }

    @Test
    @DisplayName("Test dataset with good stuff and garbage.")
    fun test6() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)
        val thirdPathFactory = S3DatasetPathFactory(TestUserID2, DatasetID3)

        val mockedS3 = mockS3List(listOf(
            // All part of the first dataset. The whole first ds should be discarded.
            firstPathFactory.datasetMetaFile(),
            "$TestUserID1/$DatasetID1/Broken-path1",
            "$TestUserID1/$DatasetID1/Broken-path2",

            // Second dataset with just meta.
            secondPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetMetaFile(),

            // Third dataset with just shares.
            thirdPathFactory.datasetShareOfferFile(TestUserID1),
            thirdPathFactory.datasetShareReceiptFile(TestUserID1)
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(2))
        assertThat(datasets.last().getShares().entries, Matchers.hasSize(1))
    }

    @Test
    @DisplayName("First file is garbage.")
    fun test7() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)
        val thirdPathFactory = S3DatasetPathFactory(TestUserID2, DatasetID3)

        val mockedS3 = mockS3List(listOf(
            // All part of the first dataset. The whole first ds should be discarded.
            "$TestUserID1/$DatasetID1/Broken-path1",
            "$TestUserID1/$DatasetID1/Broken-path2",
            firstPathFactory.datasetMetaFile(),

            // Second dataset with just meta.
            secondPathFactory.datasetMetaFile(),

            // Third dataset with meta and shares.
            thirdPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetShareOfferFile(TestUserID1),
            thirdPathFactory.datasetShareReceiptFile(TestUserID1)
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(2))
        assertThat(datasets.last().getShares().entries, Matchers.hasSize(1))
    }

    @Test
    @DisplayName("Last dataset is garbage.")
    fun test8() {
        val firstPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID1)
        val secondPathFactory = S3DatasetPathFactory(TestUserID1, DatasetID2)
        val thirdPathFactory = S3DatasetPathFactory(TestUserID2, DatasetID3)

        val mockedS3 = mockS3List(listOf(
            // First dataset with just meta
            firstPathFactory.datasetMetaFile(),

            // Second dataset with just meta.
            secondPathFactory.datasetMetaFile(),

            // Third dataset with meta and shares and broken paths.
            thirdPathFactory.datasetMetaFile(),
            thirdPathFactory.datasetShareOfferFile(TestUserID2),
            thirdPathFactory.datasetShareReceiptFile(TestUserID2),

            "$TestUserID1/$DatasetID3/Broken-path1",
            "$TestUserID1/$DatasetID3/Broken-path2",
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(2))
    }

    @Test
    @DisplayName("Only one dataset and it's broken.")
    fun test9() {
        val mockedS3 = mockS3List(listOf(
            "$TestUserID1/$DatasetID1/Broken-path1",
            "$TestUserID1/$DatasetID1/Broken-path2",
        ))
        val datasetManager = DatasetManager(mockedS3)
        val datasets = datasetManager.streamAllDatasets().toList()
        assertThat(datasets, Matchers.hasSize(0))
    }

    private fun mockS3List(objectPaths: List<String>): S3Bucket {
        val mockedS3 = mock<S3Bucket>()
        val mockedObjectContainer = mock<ObjectContainer>()
        val mockedObjectStream = mock<ObjectStream>()
        `when`(mockedS3.objects).thenReturn(mockedObjectContainer)
        `when`(mockedObjectContainer.stream(anyString())).thenReturn(mockedObjectStream)
        `when`(mockedObjectStream.stream()).thenReturn(objectPaths.stream().map { MockedS3Object(it, mockedS3) })
        return mockedS3
    }

    private class MockedS3Object(override val bucket: S3Bucket,
                                 override val headers: Headers,
                                 override val region: String?,
                                 override val baseName: String,
                                 override val dirName: String,
                                 override val eTag: String,
                                 override val size: Long,
                                 override val lastModified: OffsetDateTime?,
                                 override val tags: ObjectTagContainer,
                                 override val path: String
    ): AbstractS3Object(path, lastModified, eTag, size, region, headers, bucket) {
        constructor(path: String, bucket: S3Bucket) : this(
            bucket,
            BasicHeaders(emptyMap()),
            null,
            path.split("/").last(),
            path,
            "mock-etag",
            0L,
            OffsetDateTime.now(),
            mock<ObjectTagContainer>(),
            path
        )

        override fun delete(params: DeleteParams) {
            return
        }

        override fun exists(params: ObjectExistsParams): Boolean {
            return true
        }

        override fun stat(params: ObjectStatParams): ObjectMeta? {
            return null
        }
    }
}