#!/bin/bash

DIRNAME=`dirname $0`

DIRNAME=${DIRNAME#./}

if [ "$DIRNAME" = "." ]; then
  DIRNAME=`pwd`
elif [ "${DIRNAME:0:1}" != "/" ]; then
  DIRNAME="`pwd`/$DIRNAME"
fi

if [ "$DIRNAME" = "/" ]; then
  echo "bin dirname can't be '/'" >&2
  exit -1
fi

ROOT="${DIRNAME%/*}"
BIN="scripts"
LIB="lib"
CONFIG_DIR="config"

echo ROOT=$ROOT
echo BIN=$BIN
echo LIB=$LIB
echo CONFIG_DIR=$CONFIG_DIR

append_classpath() {
  if [ x"" = "x$CLASSPATH" ]; then
    CLASSPATH="$1"
  else
    CLASSPATH="$CLASSPATH:$1"
  fi
}

append_libdir() {
  for jar_file in $ROOT/$1/*.jar; do
    append_classpath "$jar_file"
  done
}

append_libdir $LIB
append_classpath $ROOT/$CONFIG_DIR

JAVA=`which java`

if [ x"" = x"$JAVA" -o ! -e "$JAVA" ]; then
  echo "Looking for java program: can't find java executable" >&2
  exit -1
fi

if [ ! -f "$JAVA" ]; then
  echo "Looking for java program: $JAVA isn't a file" >&2
  exit -1
fi

if [ ! -x "$JAVA" ]; then
  echo "Looking for java program: $JAVA isn't executable" >&2
  exit -1
fi
