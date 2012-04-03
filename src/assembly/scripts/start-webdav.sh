#!/usr/bin/env bash

# Start hdfs webdav interface
# 	usage ./start-webdav.sh -fs hdfs://nnserver:8020
#

BIN_DIR=`dirname "$0"`
BIN_DIR=`cd "$BIN_DIR"; pwd`


# add libs to CLASSPATH
CP="$BIN_DIR/../conf:"
for FILE in $BIN_DIR/../*.jar; do
    CP=${CP}:$FILE;
done

for FILE in $BIN_DIR/../lib/*.jar; do
    CP=${CP}:$FILE;
done

#echo java -classpath $CP org.apache.hadoop.fs.webdav.WebdavServer $*
java -classpath $CP org.apache.hadoop.fs.webdav.WebdavServer $*
