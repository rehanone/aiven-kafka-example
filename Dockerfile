FROM sbtscala/scala-sbt:eclipse-temurin-17.0.13_11_1.10.6_3.5.2
ARG SBT_VERSION=1.10.6

RUN sbt sbtVersion

RUN mkdir -p /app

ADD . /app

WORKDIR /app

RUN cd /app && sbt compile

CMD sbt run "aiven.kafka.App"
