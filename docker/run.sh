#!/usr/bin/env bash

echo "Starting spring boot server..."
export TENSORBOOT_HOME=`/usr/local/tensorboot/`
APP_JAR=`find $TENSORBOOT_HOME/ -name tensorboot*.jar -exec echo -n {} \;`
java -jar $APP_JAR
