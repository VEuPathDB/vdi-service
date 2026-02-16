package vdi.model.misc

data class ErrorTraceElement(
  val className: String,
  val methodName: String?,
  val fileName: String?,
  val lineNumber: UInt?,
) {
  constructor(e: StackTraceElement): this(
    className = e.className,
    methodName = e.methodName,
    fileName = e.fileName,
    lineNumber = e.lineNumber.toUInt(),
  )
}