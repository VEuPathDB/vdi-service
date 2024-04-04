package vdi.component.s3.paths

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Config
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.env.EnvKey
import vdi.component.s3.DatasetManager
import java.time.Duration
import java.time.Instant


class ListAllDatasetsTest {

    @Test
    @Tag("ListAllDatasets")
    fun integ() {
        val config: S3Config = S3Config(
            url       = System.getenv(EnvKey.S3.Host),
            port      = System.getenv(EnvKey.S3.Port).toUShort(),
            secure    = true,
            accessKey = System.getenv(EnvKey.S3.AccessToken),
            secretKey = System.getenv(EnvKey.S3.SecretKey),
        )
        val s3 = S3Api.newClient(config)
        val bucket = s3.buckets[BucketName("vdi-dev")]!!
        val datasetManager = DatasetManager(bucket)

        val startTime = Instant.now()
        datasetManager.streamAllDatasets()
            .forEach { println(it.datasetID) }
        println("Finished after: " + Duration.between(startTime, Instant.now()))
    }

}