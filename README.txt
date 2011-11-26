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

git ...

Build them (mvn install -DskipTests). Now you should have hadoop jars in your local .m2 repository.


1. Extract the source code and use maven to compile :

mvn package -DskipTests

Your distribution is ready in target/hdfs-over-webdav-${version}.tar.bz2

Unpack it (tar jxvf), cd bin/ and Start your webdav server:

$ bin/start-webdav.sh -fs hdfs://myserver:myport

The command-line swith -fs points to the namenode of your existing HDFS installation. If missing, a default of hdfs://localhost:8020 is assumed.

