package vdi.component.async

fun interface Job {
  suspend operator fun invoke()
}