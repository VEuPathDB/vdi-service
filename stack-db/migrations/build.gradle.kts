plugins { `java-library` }

sourceSets.main {
  java { setSrcDirs(emptyList<Any>()) }
  resources.srcDir("src/main/sql")
}
