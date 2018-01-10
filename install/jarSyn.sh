#!/bin/bash

folderPath=/usr/local/ExchangeAgg
projectURL=https://github.com/1303575952/ExchangeAgg.git

git init $folderPath
sleep 5s
if [ `ls -l /usr/local | grep ExchangeAgg | wc -l` -eq 1 ];
then
	echo $folderPath" init success"
else
	echo $folderPath" init failed"
fi

cd $folderPath
git pull $projectURL
sleep 10s
if [ `ls -l /usr/local/ExchangeAgg | grep pom.xml | wc -l` -eq 1 ];
then
	echo "ExchangeAgg pulled success"
else
	echo "ExchangeAgg pulled failed"
fi

if [ ! -f "/root/.m2/repository/io/parallec/parallec-core/0.10.6.1/parallec-core-0.10.6.1.jar" ];
then
	echo "parallec-core-0.10.6.1.jar is not exits, mvn install..."
	mvn install:install-file -Dfile=/usr/local/ExchangeAgg/lib/parallec-core-0.10.6.1.jar -DgroupId=io.parallec -DartifactId=parallec-core -Dversion=0.10.6.1 -Dpackaging=jar
	sleep 5s
	if [ ! -f "/root/.m2/repository/io/parallec/parallec-core/0.10.6.1/parallec-core-0.10.6.1.jar" ];
	then
		echo "parallec-core-0.10.6.1.jar mvn install failed"
	else
		echo "parallec-core-0.10.6.1.jar mvn install success"
	fi
else
	echo "parallec-core-0.10.6.1.jar exists"
fi

mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
sleep 10s
if [ `ls -l /usr/local/ExchangeAgg/target | grep ExchangeAgg-0.0.1-SNAPSHOT-jar-with-dependencies.jar | wc -l` -eq 1 ];
then
	echo "maven install success"
else
	echo "maven install failed"
fi

cp $folderPath/target/ExchangeAgg-0.0.1-SNAPSHOT-jar-with-dependencies.jar /root/run/
if [ `ls -l /usr/local/ExchangeAgg/target | grep ExchangeAgg-0.0.1-SNAPSHOT-jar-with-dependencies.jar | wc -l` -eq 1 ];
then
	echo "copy success"
else
	echo "copy failed"
fi
