package vdi.core.pruner

internal enum class PrunableState {
  Deleted,
  Obsoleted,
  NotPrunable,
}
