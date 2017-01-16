#!/usr/bin/env bash
docker run --name $1 -d -p 2113:2113 -p 1113:1113 -e EVENTSTORE_START_STANDARD_PROJECTIONS=true eventstore/eventstore
