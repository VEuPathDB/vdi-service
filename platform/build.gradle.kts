plugins {
  `java-platform`
}

dependencies {
  constraints {
    api("org.gusdb:fgputil-db:2.13.1")
    api("org.veupathdb.lib:jaxrs-container-core:7.1.0")
    api("org.veupathdb.lib:multipart-jackson-pojo:1.1.7")

    // VDI
    api("org.veupathdb.vdi:vdi-component-common:12.1.0")
    api("org.veupathdb.vdi:vdi-component-json:1.0.2")

    // Database
    api("com.zaxxer:HikariCP:6.1.0")
    api("com.oracle.database.jdbc:ojdbc8:23.6.0.24.10")
    api("org.postgresql:postgresql:42.7.4")

    // Logging
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.5.0")
    api("org.apache.logging.log4j:log4j-api:2.24.1")
    api("org.apache.logging.log4j:log4j-core:2.24.1")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.24.1")

    // Kotlin Extensions
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // HTTP
    api("io.foxcapades.lib:k-multipart:1.2.1")
    api("org.glassfish.jersey.core:jersey-server:3.1.9")

    // Messaging
    api("org.apache.kafka:kafka-clients:3.9.0")
    api("com.rabbitmq:amqp-client:5.22.0")

    // LDAP
    api("org.veupathdb.lib:ldap-utils:1.0.0")

    // Metrics
    api("io.prometheus:simpleclient:0.16.0")
    api("io.prometheus:simpleclient_common:0.16.0")

    // S3
    api("org.veupathdb.lib.s3:s34k-minio:0.7.1+s34k-0.11.0")

  }
}
