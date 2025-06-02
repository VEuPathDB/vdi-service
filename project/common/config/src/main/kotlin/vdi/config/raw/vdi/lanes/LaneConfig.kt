package vdi.config.raw.vdi.lanes

data class LaneConfig(
  val hardDelete: ConsumerLaneConfig?,
  val import: ConsumerLaneConfig?,
  val install: ConsumerLaneConfig?,
  val reconciliation: ProducerLaneConfig?,
  val sharing: ConsumerLaneConfig?,
  val softDelete: ConsumerLaneConfig?,
  val updateMeta: ProducerLaneConfig?,
)
