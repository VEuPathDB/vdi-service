package vdi.server.controller

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<*, ApplicationCall>.handlePostUninstall() {}