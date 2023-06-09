package org.veupathdb.vdi.test

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.awaitility.core.ConditionTimeoutException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration

class ApiTest {
    private val AuthToken: String = System.getProperty("AUTH_TOKEN")
    private val AuthTokenKey: String = "Auth-Key"
    private val ObjectMapper = ObjectMapper()

    object Lifecycle {
        @BeforeAll
        @JvmStatic
        fun setup() {
            RestAssured.baseURI = System.getProperty("BASE_URL")
        }
    }

    @Test
    fun testNoOp() {
        // Construct the metadata as a map. Another option here would be to make raml-generated types accessible here.
        val meta: Map<String, Any> = mapOf(
            Pair(
                "datasetType",
                mapOf(
                    Pair("name", "noop"),
                    Pair("version", "1.0")
                )
            ),
            Pair("name", "test-dataset"),
            Pair("summary", "Integration test for no-op plugin happy path."),
            Pair("projects", listOf("PlasmoDB")),
            Pair("dependencies", emptyList<String>())
        )
        val datasetID = given() // Setup request
            .contentType(ContentType.MULTIPART)
            .header(AuthTokenKey, AuthToken)
            .multiPart("file", File.createTempFile("no-op-file", ".txt"))
            .multiPart("meta", ObjectMapper.writeValueAsString(meta))
            // Execute request
            .`when`()
            .post("vdi-datasets")
            // Validate request and extract ID
            .then()
            .statusCode(200)
            .extract()
            .path<String>("datasetID")

        awaitImportStatus(datasetID, "complete")
        awaitInstallStatus(datasetID, "complete")
    }

    private fun awaitInstallStatus(datasetID: String, status: String) {
        try {
            await()
                .atMost(Duration.ofSeconds(30L))
                .until {
                    getInstallStatus(datasetID) == status
                }
        } catch (e: ConditionTimeoutException) {
            throw AssertionError(
                "Test failed while waiting for inst all status of $datasetID to become \"complete\". " +
                        "Current status: ${getInstallStatus(datasetID)})", e
            )
        }
    }

    private fun awaitImportStatus(datasetID: String, status: String) {
        try {
            await()
                .pollInterval(Duration.ofSeconds(1L))
                .atMost(Duration.ofSeconds(30L))
                .until {
                    getImportStatus(datasetID) == status
                }
        } catch (e: ConditionTimeoutException) {
            throw AssertionError(
                "Test failed while waiting for import status of $datasetID to become \"complete\". " +
                        "Current status: ${getImportStatus(datasetID)})", e
            )
        }
    }

    private fun getImportStatus(datasetID: String): String {
        return given()
            .header(AuthTokenKey, AuthToken)
            .get("vdi-datasets/$datasetID")
            .then()
            .statusCode(200)
            .extract()
            .path("status.import")
    }

    private fun getInstallStatus(datasetID: String): String {
        return given()
            .header(AuthTokenKey, AuthToken)
            .get("vdi-datasets/$datasetID")
            .then()
            .statusCode(200)
            .extract()
            .path("status.install[0].dataStatus")
    }
}