package vdi.model.meta

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import vdi.model.versioning.MetaStructureVersion
import vdi.model.versioning.VersionedObjectDeserializer

@JsonDeserialize(using = VersionedObjectDeserializer::class)
sealed interface VersionedMetaObject {
  @Suppress("unused")
  @get:JsonGetter(VersionKey)
  @get:JsonInclude(JsonInclude.Include.ALWAYS)
  val vdiMetaVersion: MetaStructureVersion
    get() = MetaStructureVersion.CurrentVersion

  companion object JsonKey {
    const val VersionKey = $$"$vdiMetaVersion"
  }
}