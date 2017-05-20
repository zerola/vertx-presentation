#!/bin/bash

DOCKER_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR=$(dirname "${DOCKER_DIR}")

(
  cd $PROJECT_DIR && 
  mvn clean package &&
  cp target/*-fat.jar $DOCKER_DIR &&
  cd $DOCKER_DIR &&
  docker build -t zeromic/pti2017 . &&
  rm *-fat.jar
)

