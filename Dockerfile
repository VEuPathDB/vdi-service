FROM veupathdb/alpine-dev-base:jdk-21-gradle-8.7 AS prep

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts
COPY platform platform
COPY service service
COPY lib lib

RUN gradle --no-daemon test shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

FROM amazoncorretto:21-alpine3.19

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=prep /workspace/build/libs/service.jar /service.jar
COPY service/startup.sh startup.sh

CMD /startup.sh
