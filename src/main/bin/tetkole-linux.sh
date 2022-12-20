#!/bin/sh

set -e

MAIN_JAR_FILE=TetKole.jar

export JAVA_OPTS="-Xms256m -Xmx1g"
export JAVA_OPTS="$JAVA_OPTS -Dlogging.appender.console.level=OFF -Djdk.gtk.version=2"

WORKING_DIR=$(pwd)
SCRIPT_DIR=$(dirname $0)
LIB_DIR=${SCRIPT_DIR}/../lib
LIB_DIR_RELATIVE=$(realpath --relative-to="${WORKING_DIR}" "${LIB_DIR}")
CLASSPATH=$(find ./$LIB_DIR_RELATIVE -name "*.jar" | sort | tr '\n' ':')

export JAVA_HOME=${LIB_DIR}/jre
export PATH=${JAVA_HOME}/bin:${PATH}
export JAVA_CMD="java -cp \"$CLASSPATH\" ${JAVA_OPTS} -jar "$LIB_DIR"/TetKole.jar false"

chmod -R 777 ./../lib/

${JAVA_CMD}