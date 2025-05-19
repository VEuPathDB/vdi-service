FROM veupathdb/alpine-dev-base:jdk24-gradle8.14 AS build

WORKDIR /workspace

COPY ["settings.gradle.kts", "build.gradle.kts", "./"]
COPY gradle/libs.versions.toml ./gradle/

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

RUN gradle download-dependencies

COPY schema schema
COPY lib lib
COPY module module

# Build Info
ARG GIT_TAG="unknown"
ARG GIT_COMMIT="unknown"
ARG GIT_BRANCH="unknown"
ARG GIT_URL="unknown"
ARG BUILD_ID="unknown"
ARG BUILD_NUMBER="unknown"
ARG BUILD_TIME="unknown"

RUN gradle \
    -Pbuild.git.tag="${GIT_TAG}" \
    -Pbuild.git.commit="${GIT_COMMIT}" \
    -Pbuild.git.branch="${GIT_BRANCH}" \
    -Pbuild.git.url="${GIT_URL}" \
    -Pbuild.ci.id="${BUILD_ID}" \
    -Pbuild.ci.number="${BUILD_NUMBER}" \
    -Pbuild.ci.timestamp="${BUILD_TIME}" \
    properties test shadowJar


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

CMD ["sh", "-c", "java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /opt/vdi/vdi-service.jar"]
