package vdi.module.events.routing.model

/**
 * MinIO Event Action Type
 *
 * Represents the primary action that was taken to trigger an event.
 *
 * The two modelled event action types are `s3:ObjectCreated:*` and
 * `s3:ObjectRemoved:*`.
 */
enum class MinIOEventAction {

  /**
   * `s3:ObjectCreated:*`
   */
  CREATE,

  /**
   * `s3:ObjectRemoved:*`
   */
  DELETE,
  ;
}
