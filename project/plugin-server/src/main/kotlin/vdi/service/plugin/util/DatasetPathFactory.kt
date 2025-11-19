package vdi.service.plugin.util

import java.nio.file.FileAlreadyExistsException
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists
import vdi.config.raw.vdi.PluginConfig
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.service.plugin.conf.ShortVDIConfig


class DatasetPathFactory(config: ShortVDIConfig, plugin: PluginConfig) {

  private val pathOverrides = config.installTargets.asSequence()
    .filter { it.enabled && it.datasetFileRoot != null }
    .map { it.targetName to it.datasetFileRoot!! }
    .toMap()

  private val rootDirectory = plugin.installRoot ?: "/datasets"

  private val rootPath = Path.of(rootDirectory, config.siteBuild)

  init {
    // Ensure the mount point exists, or throw an exception.  The mount point
    // should be created by the systems team when they set up the service stack.
    if (rootPath.notExists())
      throw IllegalStateException("configured user dataset directory root path '$rootPath' does not exist")

    pathOverrides.asSequence()
      .map { (name, dir) -> name to Path.of(rootDirectory, dir) }
      .forEach { (name, dir) -> if (dir.notExists())
        throw IllegalStateException("configured user dataset directory root path '$dir' does not exist for install target $name") }
  }

  // {mount-path}/{build}/{project}/{dataset-id}
  fun makePath(project: InstallTargetID, datasetID: DatasetID): Path {
    val siteDir = if (project in pathOverrides)
      Path.of(rootDirectory, pathOverrides[project]!!)
    else
      rootPath.resolve(project)

    // If this is the first time we're seeing a path for a given target project,
    // create the project directory.
    if (siteDir.notExists())
      try { siteDir.createDirectory() } catch (_: FileAlreadyExistsException) { /* Ignore race condition mkdir */ }

    return siteDir.resolve(datasetID.toString())
  }
}