package vdi.core.async

fun interface Job {
  suspend operator fun invoke()
}
