package vdi.components.datasets

interface DatasetDirectory {

  fun exists(): Boolean

  fun listUploadFiles(): List<DatasetFileHandle>

  fun listDataFiles(): List<DatasetFileHandle>

  fun listShares(): List<DatasetShare>
}
