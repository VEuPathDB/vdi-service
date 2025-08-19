package vdi.config.raw

import com.fasterxml.jackson.annotation.JsonGetter
import java.util.jar.Attributes
import java.util.jar.Manifest

data class ManifestConfig(
  val gitTag: String,
  val gitCommit: String,
  val gitBranch: String,
  @get:JsonGetter("gitUrl")
  val gitURL: String,
  @get:JsonGetter("buildId")
  val buildID: String,
  val buildNumber: String,
  val buildTime: String,
) {
  constructor(manifest: Manifest): this(manifest.mainAttributes)

  private constructor(attrs: Attributes): this(
    gitTag      = attrs.getValue("Git-Tag"),
    gitCommit   = attrs.getValue("Git-Commit"),
    gitBranch   = attrs.getValue("Git-Branch"),
    gitURL      = attrs.getValue("Git-URL"),
    buildID     = attrs.getValue("Build-ID"),
    buildNumber = attrs.getValue("Build-Number"),
    buildTime   = attrs.getValue("Build-Time"),
  )
}
