package vdi.components.common.jobs

fun interface Job {
  suspend operator fun invoke()
}