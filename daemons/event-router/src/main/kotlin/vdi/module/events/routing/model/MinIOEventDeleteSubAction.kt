package vdi.module.events.routing.model

/**
 * Event subtypes for [MinIOEventAction.DELETE]
 */
enum class MinIOEventDeleteSubAction : MinIOEventSubAction {

  /**
   * `s3:ObjectRemoved:Delete`
   */
  DELETE,

  /**
   * `s3:ObjectRemoved:DeleteMarkerCreated`
   */
  DELETE_MARKER_CREATED,
  ;
}