#!/bin/bash

. `dirname $0`/set_classpath.sh

MAINCLASS=elsie.Elsie

PROPERTIES="-Delsie.root=$ROOT "
PROPERTIES+="-Delsie.lib=$ROOT/$LIB "
PROPERTIES+="-Delsie.common.lib=$ROOT/$COMMON_LIB "
PROPERTIES+="-Delsie.server.lib=$ROOT/$SERVER_LIB "
PROPERTIES+="-Delsie.plugins.lib=$ROOT/$PLUGINS_LIB "
PROPERTIES+="-Delsie.plugins.classes=$ROOT/$PLUGINS_CLASSES "

echo $JAVA -classpath $CLASSPATH $PROPERTIES $MAINCLASS
$JAVA -classpath $CLASSPATH $PROPERTIES $MAINCLASS

