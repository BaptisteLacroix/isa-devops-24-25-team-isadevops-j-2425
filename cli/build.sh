#!/usr/bin/env bash

echo "Compiling the KiwiCard Spring CLI within a multi-stage docker build"

docker build --build-arg JAR_FILE=cli-0.0.1-SNAPSHOT.jar -t teamj/kiwicard-spring-cli .
