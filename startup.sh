#!/usr/bin/env sh

waitFor() {
  name=$1
  host=$2
  port=$3

  while ! nc -zv "$host" "$port"; do
    echo "waiting for $name to become reachable at '$host:$port'"
    sleep 5s
  done
}

waitFor kafka localhost 9092
waitFor cache-db localhost 5432
waitFor rabbit "$GLOBAL_RABBIT_HOST" "${GLOBAL_RABBIT_PORT:-5672}"
waitFor minio "$S3_HOST" "$S3_PORT"

java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar