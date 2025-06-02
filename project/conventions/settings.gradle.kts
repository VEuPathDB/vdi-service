rootProject.name = "build-conventions"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../../gradle/libs.versions.toml")) }
    create("common") { from(files("../../gradle/common.versions.toml")) }
  }
}
