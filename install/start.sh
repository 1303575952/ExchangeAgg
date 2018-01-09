#!/bin/bash

ip=`curl http://icanhazip.com`

echo "ip="$ip

function start_rocketmq_nameserver()
{
    if [ `ps -ef |grep mqnamesrv |grep bin |wc -l` -eq 0 ];
    then
        nohup sh /usr/local/rocketmq/bin/mqnamesrv -n $ip":9876" &

        sleep 5s
        
        if [ `cat ~/logs/rocketmqlogs/namesrv.log |grep "The Name Server boot success." |wc -l` -eq 1 ];
        then
            rm -rf nohup.out

            echo "RocketMQ Name Server start ... [Success]"
        else
            echo "RocketMQ Name Server start ... [Failed]"
        fi
    else
        echo "RocketMQ Name Server 已经启动"
    fi

    echo "The RocketMQ Name Server log can be found at ~/logs/rocketmqlogs/namesrv.log ..."
}

function start_rocketmq_broker()
{
    if [ `ps -ef |grep mqbroker |grep bin |wc -l` -eq 0 ];
    then

        rm -rf /usr/local/rocketmq/conf/mybroker.properties

        echo "brokerIP1="$ip >/usr/local/rocketmq/conf/mybroker.properties

        nohup sh /usr/local/rocketmq/bin/mqbroker -n $ip":9876" -c /usr/local/rocketmq/conf/mybroker.properties&

        sleep 5s
        
        if [ `cat ~/logs/rocketmqlogs/broker.log |grep "boot success" |wc -l` -eq 1 ];
        then
            rm -rf nohup.out

            echo "RocketMQ Broker start ... [Success]"
        else
            echo "RocketMQ Broker start ... [Failed]"
        fi
    else
        echo "RocketMQ Broker 已经启动"
    fi

    echo "The RocketMQ Broker log can be found at ~/logs/rocketmqlogs/broker.log ..."
}

function start_rocketmq()
{
    start_rocketmq_nameserver

    start_rocketmq_broker
}

function start_rocketmq_ui()
{
    if [ `netstat -lnp |grep 12581 |wc -l ` -eq 0 ];
    then
        nohup java -jar /usr/local/rocketmq/ui/rocketmq-console-ng-1.0.0.jar --server.port=12581 --rocketmq.config.namesrvAddr=$ip":9876" &
        
        sleep 10s
        
        if [ `netstat -lnp |grep 12581 |wc -l` -eq 1 ];
        then
            rm -rf nohup.out
            
            echo "Rocketmq WebUI start ... [Success]"

        else
            echo "Rocketmq WebUI start ... [Failed]"
        fi
    else
        echo "Rocketmq WebUI已经启动"        
    fi
    echo "You can access Rocketmq WebUI via http://"$ip":12581"
    
    echo "The RocketMQ WebUI log can be found at ~/logs/consolelogs/rocketmq-console.log ..."
}

function start_jstorm_nimbus()
{
    if [ `ps -ef |grep com.alibaba.jstorm.daemon.nimbus.NimbusServer |grep java |wc -l` -eq 0 ];
    then
        if [ `hostname -i`="127.0.0.1" ];
        then
            echo "Reset hostname to wufeng ..."
            `hostname wufeng`
        fi

        nohup jstorm nimbus &

        sleep 10s
        
        if [ `cat /usr/local/jstorm/logs/nimbus.log |grep "Successfully started nimbus" |wc -l` -eq 1 ];
        then
            rm -rf nohup.out

            echo "Jstorm Nimbus Server start ... [Success]"
        else
            echo "Jstorm Nimbus Server start ... [Failed]"
        fi
    else
        echo "Jstorm Nimbus 已经启动"
    fi

    echo "The Jstorm Nimbus log can be found at /usr/local/jstorm/logs/nimbus.log ..."
}


function start_jstorm_supervisor()
{
    if [ `ps -ef |grep com.alibaba.jstorm.daemon.supervisor.Supervisor |grep java |wc -l` -eq 0 ];
    then
        if [ `hostname -i`="127.0.0.1" ];
        then
            echo "Reset hostname to wufeng ..."
            `hostname wufeng`
        fi

        nohup jstorm supervisor &

        sleep 5s
        
        if [ `cat /usr/local/jstorm/logs/supervisor.log |grep "running jstorm in standalone mode" |wc -l` -eq 1 ];
        then
            rm -rf nohup.out

            echo "Jstorm Supervisor Server start ... [Success]"
        else
            echo "Jstorm Supervisor Server start ... [Success]"
        fi
    else
        echo "Jstorm Supervisor 已经启动"
    fi
    echo "The Jstorm Supervisor log can be found at /usr/local/jstorm/logs/supervisor.log ..."
}

function start_jstorm()
{
    start_jstorm_nimbus

    start_jstorm_supervisor
}


function start_jstorm_ui()
{
    if [ `netstat -lnp |grep 8080 |wc -l` -eq 0 ];
    then
        if [ ! -f "/root/.jstorm/storm.yaml" ];
        then
            echo "Jstorm WebUI can not find UI configuration file: /root/.jstorm/storm.yaml"
            
            mkdir -p /root/.jstorm

            cp -rf /usr/local/jstorm/conf/storm.yaml /root/.jstorm/

            echo "Copy jstorm/conf/storm.yaml to /root/.jstorm/storm.yaml for Jstorm WebUI usage"
        fi

        sh /usr/local/tomcat/bin/startup.sh
        
        sleep 10s
        
        if [ `netstat -lnp |grep 8080 |wc -l` -eq 1 ];
        then
            echo "Jstorm WebUI start ... [Success]"
        else
            echo "Jstorm WebUI start ... [Failed]"
        fi
    else
        echo "Jstorm WebUI已经启动"
    fi
    echo "You can access Jstorm WebUI via http://"$ip":8080"
    echo "The Jstorm WebUI log can be found at /usr/local/tomcat/logs/jstorm.ui.log ..."
}

function start_zookeeper()
{
    if [ `sh /usr/local/zookeeper/bin/zkServer.sh status |grep Mode |wc -l` -eq 0 ];
    then
        sh /usr/local/zookeeper/bin/zkServer.sh start
        
        sleep 5s
        
        zookeeper_log=`find / -name "zookeeper.out"`

        if [ `sh /usr/local/zookeeper/bin/zkServer.sh status |grep Mode |wc -l` -eq 1 ];
        then
            echo "Zookeeper start ... [Success]"
        else
            echo "Zookeeper start ... [Failed]"
        fi
    else
        echo "Zookeeper已经启动"
    fi
    echo "The Zookeeper log can be found at "$zookeeper_log" ..."
}

function start_influxdb()
{
    if [ `/bin/systemctl status influxdb.service |grep "active (running)" |wc -l` -eq 0 ];
    then
        /bin/systemctl start influxdb.service
        
        if [ `service influxdb status |grep "active (running)" |wc -l` -eq 1 ];
        then
            echo "Influxdb start ... [Success]"
        else
            echo "Influxdb start ... [Failed]"
        fi
    else
        echo "Influxdb已经启动"

    fi
    echo "The Influxdb log can be found at /var/log/influxdb/influxd.log ..."    
}

function start_grafana()
{
    if [ `service grafana-server status |grep "active (running)" |wc -l` -eq 0 ];
    then
        service grafana-server start

        if [ `service grafana-server status |grep "active (running)" |wc -l` -eq 1 ];
        then
            echo "Grafana start ... [Success]"
        else
            echo "Grafana start ... [Failed]"
        fi
    else
        echo "Grafana已经启动"

    fi
    echo "You can access Grafana via http://"$ip":3000"
    echo "The Grafana log can be found at /var/log/grafana ..."    
}


function start_aio()
{
    start_rocketmq

    start_rocketmq_ui

    start_zookeeper
    
    start_jstorm

    start_jstorm_ui

    start_influxdb

    start_grafana
}

start_aio
