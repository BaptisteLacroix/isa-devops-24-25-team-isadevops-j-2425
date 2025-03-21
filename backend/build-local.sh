#!/usr/bin/env bash

echo "Compiling the Kiwi Card Spring BACKEND within a multi-stage docker build"

docker build --build-arg JAR_FILE=kiwicard-0.0.1-SNAPSHOT.jar -t teamj/kiwicard-spring-backend -f Dockerfile-local .
