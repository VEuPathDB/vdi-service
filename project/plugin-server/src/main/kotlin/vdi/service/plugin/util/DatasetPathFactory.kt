package vdi.service.plugin.util

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import java.nio.file.FileAlreadyExistsException
import kotlin.io.path.notExists
import vdi.config.raw.vdi.PluginConfig
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID
import vdi.service.plugin.conf.ShortVDIConfig


class DatasetPathFactory(config: ShortVDIConfig, plugin: PluginConfig) {

  private val pathOverrides = config.installTargets.asSequence()
    .filter { it.enabled && it.datasetFileRoot != null }
    .associate { it.targetName to it.datasetFileRoot!! }

  private val rootDirectory = plugin.installRoot ?: "/datasets"

  private val rootPath = Path(rootDirectory, config.siteBuild)

  init {
    // Ensure the mount point exists, or throw an exception.  The mount point
    // should be created by the systems team when they set up the service stack.
    if (rootPath.toJavaPath().notExists())
      throw IllegalStateException("configured user dataset directory root path '$rootPath' does not exist")

    pathOverrides.asSequence()
      .map { (name, dir) -> name to Path(rootDirectory, dir) }
      .forEach { (name, dir) -> if (dir.toJavaPath().notExists())
        throw IllegalStateException("configured user dataset directory root path '$dir' does not exist for install target $name") }
  }

  // {mount-path}/{build}/{project}/{dataset-id}
  fun makePath(project: InstallTargetID, datasetID: DatasetID): Path {
    val siteDir = if (project in pathOverrides)
      Path(rootDirectory, pathOverrides[project]!!)
    else
      Path(rootPath, project)

    // If this is the first time we're seeing a path for a given target project,
    // create the project directory.

    if (SystemFileSystem.exists(siteDir))
      try { SystemFileSystem.createDirectories(siteDir) } catch (_: FileAlreadyExistsException) { /* Ignore race condition mkdir */ }

    return Path(siteDir, datasetID.toString())
  }
}