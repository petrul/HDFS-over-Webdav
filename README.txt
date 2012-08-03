HDFS over WEBDAV
================

This code is a webdav interface to Hadoop HDFS and is a modified version of http://www.hadoop.iponweb.net.

This version of hdfs-webdav works with Hadoop 2.0.0-alpha

INSTALLATION
============

Get 2.0.0-alpha Hadoop jars into your local repository. The exact version required is mentioned in the pom.xml :
2.0.0-alpha. One way of doing it is getting hadoop source and compiling:

    git clone -b release-2.0.0-alpha git://github.com/apache/hadoop-common.git

Build Hadoop :

	$ mvn install -Pdist -DskipTests 
	
Now you should have hadoop jars in your local .m2 repository.


Extract the source code and use maven to compile :

	mvn package

and then build the distribution:

	mvn assembly:single

Your distribution is ready in target/hdfs-over-webdav-${version}.tar.bz2

Unpack it (tar jxvf) and start your webdav server:

	$ bin/start-webdav.sh -fs hdfs://myserver:myport

The command-line swith -fs points to the namenode of your existing HDFS installation. If missing, a default of hdfs://localhost:8020 is assumed.
You have help with the -h switch.
