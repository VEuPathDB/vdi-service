plugins { `java-library` }

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
}

sourceSets.main {
  java { setSrcDirs(emptyList<Any>()) }
  resources.srcDir("src/main/sql")
}
