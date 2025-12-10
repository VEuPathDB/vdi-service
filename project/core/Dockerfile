FROM veupathdb/alpine-dev-base:jdk25-gradle9.2 AS build

WORKDIR /workspace

COPY gradle gradle
COPY project/conventions project/conventions
RUN cd project/conventions && gradle --no-daemon build

ARG GH_USERNAME
ARG GH_TOKEN

COPY project/common project/common
RUN cd project/common && gradle --no-daemon :json:build :util:build :model:build :logging:build :config:build :target-db:build

COPY settings.gradle.kts .
COPY stack-db/migrations stack-db/migrations
COPY [ "project/core/build.gradle.kts", "project/core/settings.gradle.kts", "project/core/" ]

RUN gradle --no-daemon :core:download-dependencies

# Build Info
ARG GIT_TAG="unknown"
ARG GIT_COMMIT="unknown"
ARG GIT_BRANCH="unknown"
ARG GIT_URL="unknown"
ARG BUILD_ID="unknown"
ARG BUILD_NUMBER="unknown"
ARG BUILD_TIME="unknown"

COPY project/core project/core

RUN gradle --no-daemon \
    -Pbuild.git.tag="${GIT_TAG}" \
    -Pbuild.git.commit="${GIT_COMMIT}" \
    -Pbuild.git.branch="${GIT_BRANCH}" \
    -Pbuild.git.url="${GIT_URL}" \
    -Pbuild.ci.id="${BUILD_ID}" \
    -Pbuild.ci.number="${BUILD_NUMBER}" \
    -Pbuild.ci.timestamp="${BUILD_TIME}" \
    :core:properties :core:test :core:shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM amazoncorretto:25-alpine3.22 AS runtime

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=build ["/workspace/project/core/build/libs/vdi-service.jar", "/opt/vdi/"]


CMD ["sh", "-c", "java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /opt/vdi/vdi-service.jar"]
