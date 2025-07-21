package vdi.model.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.model.compat.Upgrade_InstallTarget_StringToObj

@JsonDeserialize(using = Upgrade_InstallTarget_StringToObj::class)
data class DatasetInstallTarget(
  @field:JsonProperty(TargetID)
  val targetID: InstallTargetID,

  @field:JsonProperty(BuildInfo)
  val buildInfo: String?
) {
  companion object JsonKey {
    const val TargetID = "targetId"
    const val BuildInfo = "buildInfo"
  }
}
