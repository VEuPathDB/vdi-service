package vdi.core.s3.util

interface PathPredicate {
  fun matches(path: String): Boolean
}