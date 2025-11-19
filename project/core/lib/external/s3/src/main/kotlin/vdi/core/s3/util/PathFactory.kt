package vdi.core.s3.util

import vdi.core.s3.paths.DatasetPath

interface PathFactory<P: DatasetPath>: PathPredicate {
  fun create(path: String): P
}
