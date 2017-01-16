#!/usr/bin/env bash
CONTAINER_NAME=$1
CONTAINER_ID=$(docker ps -a -q --filter="name=$CONTAINER_NAME")

if [ -n "$CONTAINER_ID" ]; then
    echo $CONTAINER_ID
    docker rm $(docker stop $CONTAINER_ID)
else
    echo "no docker containers running image '$CONTAINER_NAME'."
fi
