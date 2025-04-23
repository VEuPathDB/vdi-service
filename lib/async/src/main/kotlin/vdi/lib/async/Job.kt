package vdi.lib.async

fun interface Job {
  suspend operator fun invoke()
}
