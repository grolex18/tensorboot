#!/usr/bin/env bash

# Local vars
C=false
D=

# Read cmd line parameters
while getopts ":cd:" o
do case "$o" in
	c) C=true;;
	d) D='-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5050,suspend=n';;
	*) echo "
Usage: $0 [-c -d]
   -c Recompile project
   -d Enable debug
" >&2
		exit 1;;
	esac
done

if [[ $C == true ]]; then
	echo "Recompiling the project"
	./mvnw clean install -DskipTests=true
fi

if [[ $? == 0 ]]; then
	echo "Starting spring boot server..."
	export TENSORBOOT_HOME=`dirname $0`
	APP_JAR=`find $TENSORBOOT_HOME/target/ -name tensorboot*.jar -exec echo -n {} \;`
	CLASSPATH=$CLASSPATH:$APP_JAR
	java $D -jar $APP_JAR
fi
