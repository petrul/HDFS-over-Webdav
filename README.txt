HDFS over WEBDAV
================

This code is webdav for Hadoop 0.23. It is forked from gensth who forked from huyphan.

This code is the modified version of http://www.hadoop.iponweb.net to make it compatible with new version of Hadoop.
This version of hdfs-webdav works with Hadoop 0.23, you can checkout the previous commits on my git repository for older version of Hadoop.

INSTALLATION
============

2. Get 0.23 Hadoop jars into your local repository. The exact version mentioned in the pom.xml is 
0.23-SNAPSHOT. One way of doing it is getting hadoop source and compiling:

svn co https://svn.apache.org/repos/asf/hadoop/common/branches/branch-0.23/

or

git 

1. Extract the source code and Use maven to compile :

mvn package

3. Modify config file hadoop-webdav.sh to satisfy your configuration:

    * HADOOP_WEBDAV_HOST, HADOOP_WEBDAV_PORT - address and port WebDAV server will listen to.
    * HADOOP_WEBDAV_HDFS - The name of the HDFS, e.g. namenode:port in case if you run WebDAV server on nodes that are different from the master. If this parameter is not specified, WebDAV will try determine name of the FS from 'fs.default.name' parameter, specified in hadoop-site.xml of your Hadoop installation.
    * HADOOP_WEBDAV_CLASSPATH parameter should point to lib directory from where you unpacked WebDAV distribution.

4. Start your webdav server:
$ /opt/hadoop-0.20.1/bin/start-webdav.sh


Huy Phan <dachuy@gmail.com>
Petru Dimulescu <petru.dimulescu@gmail.com>
