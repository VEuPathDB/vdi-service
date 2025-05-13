FROM veupathdb/alpine-dev-base:jdk24-gradle8.14 AS build

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

COPY ["service/settings.gradle.kts", "service/build.gradle.kts", "./"]
COPY service/gradle/libs.versions.toml gradle/libs.versions.toml
COPY service/buildSrc buildSrc

RUN gradle --no-daemon download-dependencies

COPY service/schema schema
COPY service/lib lib
COPY service/module module

RUN gradle --no-daemon test shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM amazoncorretto:24-alpine3.20 AS runtime

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=build ["/workspace/build/libs/vdi-service.jar", "/opt/vdi/"]

ARG CONFIG_FILE="config/halfway-config.yml"
COPY ${CONFIG_FILE} /etc/vdi/config.yml

CMD java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /opt/vdi/vdi-service.jar
