plugins {
  `java-platform`
}

dependencies {
  constraints {
    api("org.gusdb:fgputil-db:2.12.11")
    api("org.veupathdb.lib:jaxrs-container-core:6.17.0")
    api("org.veupathdb.lib:multipart-jackson-pojo:1.1.0")

    // VDI
    api("org.veupathdb.vdi:vdi-component-common:6.2.0")
    api("org.veupathdb.vdi:vdi-component-json:1.0.0")

    // Database
    api("com.zaxxer:HikariCP:5.0.1")
    api("com.oracle.database.jdbc:ojdbc8:21.9.0.0")
    api("org.postgresql:postgresql:42.5.4")

    // Logging
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")
    api("org.apache.logging.log4j:log4j-api:2.20.0")
    api("org.apache.logging.log4j:log4j-core:2.20.0")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // Kotlin Extensions
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // HTTP
    api("io.foxcapades.lib:k-multipart:1.2.0")
    api("org.glassfish.jersey.core:jersey-server:3.1.1")

    // JSON
    api("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    api("com.fasterxml.jackson.core:jackson-annotations:2.14.2")

    // Messaging
    api("org.apache.kafka:kafka-clients:3.4.0")
    api("com.rabbitmq:amqp-client:5.17.0")

    // LDAP
    api("org.veupathdb.lib:ldap-utils:1.0.0")

    // Metrics
    api("io.prometheus:simpleclient:0.16.0")
    api("io.prometheus:simpleclient_common:0.16.0")

    // S3
    api("org.veupathdb.lib.s3:s34k-minio:0.6.0+s34k-0.10.0")

  }
}
