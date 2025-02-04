#!/usr/bin/env bash

echo "Compiling the Kiwi Card Spring BACKEND within a multi-stage docker build"

docker build --build-arg JAR_FILE=kiwiCard-0.0.1-SNAPSHOT.jar -t teamj/kiwicard-spring-backend .
