FROM veupathdb/alpine-dev-base:jdk24-gradle8.14 AS prep

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

COPY ["service/settings.gradle.kts", "service/build.gradle.kts", "./"]
COPY service/gradle/libs.versions.toml gradle/libs.versions.toml
COPY service/buildSrc buildSrc

RUN gradle --no-daemon download-dependencies

COPY service/bootstrap bootstrap
COPY service/schema schema
COPY service/lib lib
COPY service/module module

RUN gradle --no-daemon test shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM amazoncorretto:24-alpine3.20

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=prep ["/workspace/build/libs/service.jar", "/workspace/bootstrap/startup.sh", "/"]

ARG CONFIG_FILE="config/production-config.yml"
COPY ${CONFIG_FILE} /etc/vdi/config.yml

CMD /startup.sh
