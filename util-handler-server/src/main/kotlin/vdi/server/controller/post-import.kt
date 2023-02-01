package vdi.server.controller

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import java.io.File

data class PostImportRequestContext(
  val controller:   ApplicationCall,
  val vdiID:        String,
  val workspace:    File,
  val inputArchive: File,
)

/**
 * Handles Import `POST` Requests
 */
suspend fun PipelineContext<*, ApplicationCall>.handlePostImport() {
}
