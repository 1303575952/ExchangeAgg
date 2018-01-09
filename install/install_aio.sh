#!/bin/bash
#得到时间
TIME_FLAG=`date +%Y%m%d_%H%M%S`
#备份配置文件
cp /etc/profile /etc/profile.bak_$TIME_FLAG


function ins_jdk()
{
    if [ -z "${JAVA_HOME}" ];
    then
        echo "Begin to install JDK, Please waiting..."
        
        if [ ! -f "jdk-8u151-linux-x64.tar.gz" ];  
        then  
            wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u151-b12/e758a0de34e24606bca991d704f6dcbf/jdk-8u151-linux-x64.tar.gz
        fi
    
        if [ ! -d "jdk1.8.0_151" ];  
        then  
            tar -zxvf jdk-8u151-linux-x64.tar.gz 
        fi  
        
        rm -rf /usr/local/java

        mv jdk1.8.0_151 /usr/local/java
    
        echo "######################################"
        echo "Begin to config environment variables,please waiting..."
        echo "######################################"
        #修改环境变量，直接写入配置文件
        echo "#java" >>/etc/profile

        echo "JAVA_HOME=/usr/local/java" >>/etc/profile

        source /etc/profile

        echo "PATH=$PATH:$JAVA_HOME/bin" >>/etc/profile

        source /etc/profile

        echo "CLASSPATH=.:$JAVA_HOME/lib" >> /etc/profile

        source /etc/profile

        echo "export JAVA_HOME  PATH CLASSPATH" >>/etc/profile

        source /etc/profile

        rm -rf jdk-8u151-linux-x64.tar.gz

        echo "JDK安装成功"
    else
        echo "本机已安装JDK无需再次安装"
    fi
    
}


function ins_maven()
{

    if [ -z "${M2_HOME}" ];
    then
        echo "Begin to install maven, Please waiting..."
        if [ ! -f "apache-maven-3.3.9-bin.tar.gz" ];  
        then
            wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
        fi

        tar -zxvf apache-maven-3.3.9-bin.tar.gz
        
        rm -rf /usr/local/maven

        mv -f apache-maven-3.3.9 /usr/local/maven

        echo "######################################"
        echo "Begin to config environment variables,please waiting..."
        echo "######################################"
        #修改环境变量，直接写入配置文件

        echo "#maven" >>/etc/profile
        
        echo "M2_HOME=/usr/local/maven" >> /etc/profile
        
        source /etc/profile

        echo "PATH=$PATH:$M2_HOME/bin" >>/etc/profile

        source /etc/profile

        rm -rf apache-maven-3.3.9-bin.tar.gz
        echo "MAVEN安装成功"
    else
        echo "本机已安装maven无需再次安装"
    fi
}

function ins_rocketmq()
{
    if [ ! -d "/usr/local/rocketmq" ];  
    then  
        echo "Begin to install RocketMQ, Please waiting..."

        wget http://mirror.bit.edu.cn/apache/rocketmq/4.2.0/rocketmq-all-4.2.0-bin-release.zip

        unzip rocketmq-all-4.2.0-bin-release.zip -d rocketmq

        rm -rf /usr/local/rocketmq

        mv -f rocketmq /usr/local/rocketmq

        rm -rf rocketmq-all-4.2.0-bin-release.zip
        
        echo "RocketMQ安装成功"
    else
        echo "本机已安装RocketMQ无需再次安装"
    fi

}

function ins_git()
{

    if [ `rpm -qa | grep git- |wc -l` -eq 0 ];
    then
        echo "Begin to install Git, Please waiting..."

        yum -y install git
        
        echo "Git安装成功"
    else
        echo "本机已安装Git无需再次安装"
    fi
}

function ins_rocketmq_ui()
{
    if [ ! -d "/usr/local/rocketmq/ui" ];  
    then 
        echo "Begin to install RocketMQ WebUI,Please waiting..."

        echo "######################################"
        echo "Download rocketmq-externals from github,please waiting..."
        echo "######################################"
	
    	git clone https://github.com/apache/rocketmq-externals.git

    	cd rocketmq-externals/rocketmq-console

        echo "######################################"
        echo "Maven package rocketmq-console,please waiting..."
        echo "######################################"

	    mvn clean package -Dmaven.test.skip=true

	    mkdir -p /usr/local/rocketmq/ui

	    cp -rf target/rocketmq-console-ng-1.0.0.jar /usr/local/rocketmq/ui
    
        cd ~

        rm -rf rocketmq-externals

        echo "RocketMQ WebUI安装成功"
    else
        echo "本机已安装RocketMQ WebUI无需再次安装"
    fi
}

function ins_zookeeper()
{   
    if [ -z "${ZOOKEEPER_HOME}" ];  
    then 
        echo "Begin to install Zookeeper,Please waiting..."

        if [ ! -f "zookeeper-3.4.10.tar.gz" ];  
        then  
	        wget http://mirrors.hust.edu.cn/apache/zookeeper/zookeeper-3.4.10/zookeeper-3.4.10.tar.gz
        fi

	    tar -zxvf zookeeper-3.4.10.tar.gz 

        rm -rf rocketmq

	    mv -f zookeeper-3.4.10 zookeeper

	    cp zookeeper/conf/zoo_sample.cfg zookeeper/conf/zoo.cfg

        rm -rf /usr/local/zookeeper

        mv -f zookeeper /usr/local/zookeeper

        echo "#zookeeper" >>/etc/profile
        
        echo "ZOOKEEPER_HOME=/usr/local/zookeeper" >> /etc/profile
    
        echo "PATH=$PATH:$ZOOKEEPER_HOME/bin" >>/etc/profile

        rm -rf zookeeper-3.4.10.tar.gz

        echo "Zookeeper安装成功"
    else
        echo "本机已安装Zookeeper无需再次安装"
    fi        
}

function ins_jstorm()
{
    if [ ! -d "/usr/local/jstorm" ];  
    then
        echo "Begin to install Jstorm,Please waiting..."

        if [ ! -f "jstorm-2.2.1.zip" ];  
        then
	        wget https://github.com/alibaba/jstorm/releases/download/2.2.1/jstorm-2.2.1.zip
        fi

	    unzip jstorm-2.2.1.zip -d jstorm

	    mv -f jstorm/jstorm-2.2.1 /usr/local/jstorm

	    rm -rf jstorm	

        echo "#zookeeper" >>/etc/profile

        echo "JSTORM_HOME=/usr/local/jstorm" >> /etc/profile  

        source /etc/profile

        echo "PATH=$PATH:$JSTORM_HOME/bin" >>/etc/profile
        
        source /etc/profile

	    rm -rf jstorm-2.2.1.zip

        echo "Jstorm安装成功"
    else
        echo "本机已安装Jstorm无需再次安装"
    fi         
}

function ins_tomcat()
{
    if [ ! -d "/usr/local/tomcat" ];  
    then 
        echo "Begin to install Tomcat, Please waiting..."
        
        if [ ! -f "apache-tomcat-9.0.2.tar.gz" ];  
        then
    	    wget http://mirrors.shuosc.org/apache/tomcat/tomcat-9/v9.0.2/bin/apache-tomcat-9.0.2.tar.gz
        fi

	    tar -zxvf apache-tomcat-9.0.2.tar.gz
	
	    chmod +x apache-tomcat-9.0.2/bin/*

    	mv -f apache-tomcat-9.0.2 /usr/local/tomcat

        rm -rf apache-tomcat-9.0.2.tar.gz
        echo "Tomcat安装成功"
    else
        echo "本机已安装Tomcat无需再次安装"
    fi          
}


function ins_jstorm_ui()
{
    if [ ! -f "/usr/local/tomcat/webapps/jstorm-ui-2.2.1.war" ];
    then
        echo "Begin to install Jstorm WebUI, Please waiting..."

        cp -rf /usr/local/jstorm/jstorm-ui-2.2.1.war /usr/local/tomcat/webapps/

        mv -f /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.old

        ln -s /usr/local/tomcat/webapps/jstorm-ui-2.2.1 /usr/local/tomcat/webapps/ROOT
        echo "Jstorm WebUI设置成功"
    else
        echo "本机已安装Jstorm WebUI无需再次安装"
    fi
}

function ins_influxdb()
{
    if [ `rpm -qa | grep influx |wc -l` -eq 0 ];
    then
        echo "Begin to install InfluxDB, Please waiting..."

        wget https://dl.influxdata.com/influxdb/releases/influxdb-1.4.2.x86_64.rpm

        yum -y localinstall influxdb-1.4.2.x86_64.rpm

        rm -y influxdb-1.4.2.x86_64.rpm
        echo "InfluxDB安装成功"
    else
        echo "本机已安装InfluxDB无需再次安装"
    fi 

}


function ins_grafana()
{
    if [ `rpm -qa | grep grafana |wc -l` -eq 0 ];
    then
        echo "Begin to install Grafana, Please waiting..."

        wget https://s3-us-west-2.amazonaws.com/grafana-releases/release/grafana-4.6.3-1.x86_64.rpm 

        yum -y localinstall grafana-4.6.3-1.x86_64.rpm

        rm -y grafana-4.6.3-1.x86_64.rpm
        echo "Grafana安装成功"
    else
        echo "本机已安装Grafana无需再次安装"
    fi 

}


function ins_aio()
{
    ins_jdk

    ins_maven

    ins_rocketmq

    ins_git

    ins_rocketmq_ui

    ins_zookeeper

    ins_jstorm

    ins_tomcat

    ins_jstorm_ui

    ins_influxdb

    ins_grafana
}

ins_aio