package vdi.module.events.routing.model

/**
 * Event subtypes for [MinIOEventAction.CREATE].
 */
enum class MinIOEventCreateSubAction : MinIOEventSubAction {

  /**
   * `s3:ObjectCreated:Put`
   */
  PUT,

  /**
   * `s3:ObjectCreated:Post`
   */
  POST,

  /**
   * `s3:ObjectCreated:Copy`
   */
  COPY,

  /**
   * `s3:ObjectCreated:CompleteMultipartUpload`
   */
  COMPLETE_MULTIPART_UPLOAD,
  ;
}