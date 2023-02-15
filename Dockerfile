# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM veupathdb/alpine-dev-base:jdk-18-gradle-7.5.1 AS prep

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

RUN apk add --no-cache git npm \
    && git config --global advice.detachedHead false

COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts
COPY components components
COPY service service

RUN gradle --no-daemon test shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM amazoncorretto:18-alpine3.16

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=prep /workspace/build/libs/service.jar /service.jar

CMD java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar
