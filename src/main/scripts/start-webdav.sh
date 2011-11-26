#!/usr/bin/env bash

# Start hadoop webdav daemon. Run this on master node.

usage="Usage: start-webdav.sh"

BIN_DIR=`dirname "$0"`
BIN_DIR=`cd "$BIN_DIR"; pwd`


# add libs to CLASSPATH
CP=""
for FILE in $BIN_DIR/../*.jar; do
    CP=${CP}:$FILE;
done

for FILE in $BIN_DIR/../lib/*.jar; do
    CP=${CP}:$FILE;
done

echo java -classpath $CP org.apache.hadoop.fs.webdav.WebdavServer $*
java -classpath $CP org.apache.hadoop.fs.webdav.WebdavServer $*
