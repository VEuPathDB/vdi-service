package vdi.lib.s3.files

interface DatasetFlagFile : DatasetFile {
  fun touch()
}
