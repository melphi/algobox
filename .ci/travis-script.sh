#!/bin/bash

set -e -x

docker build -t dainco/algobox-scheduler ./docker/algobox-scheduler
docker build -t dainco/algobox-gateway ./docker/algobox-gateway

cd java/algobox
gradle clean test

gradle algobox-api:shadowJar
docker build -t dainco/algobox-api ./algobox-api

gradle algobox-datacollector:shadowJar
docker build -t dainco/algobox-datacollector ./algobox-datacollector

cd ../../python
docker build -t dainco/algobox-jupyter ./algobox

if [[ "${TRAVIS_BRANCH}" == "master" ]]; then
  docker login -u="${DOCKER_USERNAME}" -p="${DOCKER_PASSWORD}";
  docker push dainco/algobox-api
  docker push dainco/algobox-datacollector
  docker push dainco/algobox-gateway
  docker push dainco/algobox-jupyter
  docker push dainco/algobox-scheduler
fi
