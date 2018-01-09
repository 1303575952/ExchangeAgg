#!/bin/bash


function stop_rocketmq_nameserver()
{
    if [ `ps -ef |grep mqnamesrv |grep bin |wc -l` -eq 1 ];
    then
        sh /usr/local/rocketmq/bin/mqshutdown namesrv
    else
        echo "No nameserver running... "
    fi
}

function stop_rocketmq_broker()
{
    if [ `ps -ef |grep mqbroker |grep bin |wc -l` -eq 1 ];
    then
        sh /usr/local/rocketmq/bin/mqshutdown broker
    else
        echo "No mqbroker running... "
    fi
}

function stop_rocketmq()
{
    stop_rocketmq_nameserver

    stop_rocketmq_broker
}

function stop_rocketmq_ui()
{
    if [ `netstat -lnp |grep 12581 |wc -l` -eq 1 ];
    then
        kill -9 `netstat -lnp |grep 12581 |awk -F " " '{print $7}' | awk -F "/" '{print $1}'`
    else
        echo "No RocketMQ WebUI... "
    fi
}

function stop_jstorm_nimbus()
{
    if [ `ps -ef |grep com.alibaba.jstorm.daemon.nimbus.NimbusServer |grep java |wc -l` -eq 1 ];
    then
        echo "Kill Jstorm Nimbus Server ... " 
        
        kill -9  `ps -ef |grep com.alibaba.jstorm.daemon.nimbus.NimbusServer |grep java |awk -F " " '{print $2}'`
    else
        echo "No Jstorm Nimbus Server running... "
    fi
}

function stop_jstorm_supervisor()
{
    if [ `ps -ef |grep com.alibaba.jstorm.daemon.supervisor.Supervisor |grep java |wc -l` -eq 1 ];
    then
        echo "Kill Jstorm Supervisor ... " 
        
        kill -9  `ps -ef |grep com.alibaba.jstorm.daemon.supervisor.Supervisor |grep java |awk -F " " '{print $2}'`
    else
        echo "No Jstorm Supervisor running... "
    fi
}

function stop_jstorm()
{
    stop_jstorm_nimbus

    stop_jstorm_supervisor
}


function stop_jstorm_ui()
{
    if [ `netstat -lnp |grep 8080 |wc -l` -eq 1 ];
    then
        kill -9 `netstat -lnp |grep 8080 |awk -F " " '{print $7}' | awk -F "/" '{print $1}'`
    else
        echo "No Jstorm WebUI running... "
    fi
}

function stop_zookeeper()
{
    if [ `netstat -lnp |grep 2181 |wc -l` -eq 1 ];
    then
        kill -9 `netstat -lnp |grep 2181 |awk -F " " '{print $7}' | awk -F "/" '{print $1}'`
    else
        echo "No Zookeeper running... "
    fi
}

function stop_influxdb()
{
    if [ `/bin/systemctl status influxdb.service |grep "active (running)" |wc -l` -eq 1 ];
    then
        /bin/systemctl stop influxdb.service
    else
        echo "No Zookeeper running... "
    fi
}

function stop_grafana()
{
    if [ `service grafana-server status |grep "active (running)" |wc -l` -eq 1 ];
    then
        service grafana-server stop
    else
        echo "No Grafana running... "
    fi
}

function stop_aio()
{
    stop_rocketmq

    stop_rocketmq_ui

    stop_jstorm

    stop_jstorm_ui

    stop_zookeeper

    stop_influxdb

    stop_grafana
}

function clean_logs()
{
    echo "Delete RocketMQ logs at ~/logs/rocketmqlogs/"
    rm -rf ~/logs/rocketmqlogs/*

    echo "Delete Jstorm logs at /usr/local/jstorm/logs/"
    rm -rf /usr/local/jstorm/logs/*

    echo "Delete Jstorm WebUI logs at /usr/local/tomcat/logs/"
    rm -rf /usr/local/tomcat/logs/*
    
    zookeeper_log=`find / -name "zookeeper.out"`
    echo "Delete Zookeeper logs at "$zookeeper_log
    rm -rf $zookeeper_log

    echo "Delete InfluxDB logs at /var/log/influxdb/"
    rm -rf /var/log/influxdb/*

    echo "Delete Grafana logs at /var/log/grafana/"
    rm -rf /var/log/grafana/*
}

stop_aio

clean_logs
