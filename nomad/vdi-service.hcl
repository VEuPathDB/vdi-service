job "vdi" {
  type = "service"

  group "vdi" {
    count = 1

    network {
      port "public-http" {
        to = 80
      }
      port "postgres" {
        to = 5432
      }
      port "kafka" {
        to = 9092
      }
      port "plugin-example" {
        to = "81"
      }
    }

    service {
      name     = "vdi-svc"
      port     = "public-http"
      provider = "nomad"
    }

    task "vdi-service" {
      driver = "docker"

      config {
        image = "veupathdb/vdi-service:latest"
        ports = ["public-http"]
      }
    }

    task "vdi-cache-db" {
      driver = "docker"

      config {
        image = "veupathdb/vdi-internal-db:latest"
        ports = ["postgres"]
      }
    }

    task "vdi-kafka" {
      driver = "docker"

      config {
        image = "veupathdb/apache-kafka:3.4.0"
        ports = ["kafka"]
      }
    }

    task "vdi-plugin-example" {
      driver = "docker"

      config {
        image = "veupathdb/vdi-plugin-example:latest"
        ports = ["plugin-example"]
      }

      env {
        CACHE_DB_USERNAME = "someUser"
        CACHE_DB_PASSWORD = "somePassword"
        CACHE_DB_NAME     = "vdi"
        CACHE_DB_HOST     = "localhost"
        CACHE_DB_PORT     = 5432

        KAFKA_SERVERS = "localhost:9092"
      }
    }
  }
}