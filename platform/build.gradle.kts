plugins {
  `java-platform`
}

dependencies {
  constraints {
    api("org.gusdb:fgputil-db:2.13.1")
    api("org.veupathdb.lib:jaxrs-container-core:7.0.9")
    api("org.veupathdb.lib:multipart-jackson-pojo:1.1.7")

    // VDI
    api("org.veupathdb.vdi:vdi-component-common:11.0.0")
    api("org.veupathdb.vdi:vdi-component-json:1.0.2")

    // Database
    api("com.zaxxer:HikariCP:5.1.0")
    api("com.oracle.database.jdbc:ojdbc8:23.3.0.23.09")
    api("org.postgresql:postgresql:42.7.3")

    // Logging
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
    api("org.apache.logging.log4j:log4j-api:2.23.1")
    api("org.apache.logging.log4j:log4j-core:2.23.1")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.23.1")

    // Kotlin Extensions
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // HTTP
    api("io.foxcapades.lib:k-multipart:1.2.1")
    api("org.glassfish.jersey.core:jersey-server:3.1.5")

    // JSON
    api("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    api("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    // Messaging
    api("org.apache.kafka:kafka-clients:3.7.0")
    api("com.rabbitmq:amqp-client:5.20.0")

    // LDAP
    api("org.veupathdb.lib:ldap-utils:1.0.0")

    // Metrics
    api("io.prometheus:simpleclient:0.16.0")
    api("io.prometheus:simpleclient_common:0.16.0")

    // S3
    api("org.veupathdb.lib.s3:s34k-minio:0.7.1+s34k-0.11.0")

  }
}
