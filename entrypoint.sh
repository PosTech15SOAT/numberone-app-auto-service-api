#!/bin/sh
set -e

exec java \
  -javaagent:/app/dd-java-agent.jar \
  -jar /app/app.jar