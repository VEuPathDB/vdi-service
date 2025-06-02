package vdi.lib.s3.util

import vdi.lib.s3.paths.DatasetPath

interface PathFactory<P: DatasetPath<*>>: PathPredicate {
  fun create(path: String): P
}
