################################################
#  Start RocketMQ Step 0
#      Shutdown and cleanup
################################################
sh /root/Coin/apache-rocketmq/bin/mqshutdown broker
sh /root/Coin/apache-rocketmq/bin/mqshutdown namesrv
rm -rf /root/logs/rocketmqlogs/*

################################################
#  Start RocketMQ Step 1
#      Start Name Server
################################################
nohup sh /root/Coin/apache-rocketmq/bin/mqnamesrv -n 182.92.150.57:9876 &
sleep 10s
nameServerStatus=`cat /root/logs/rocketmqlogs/namesrv.log  |grep "The Name Server boot success" |wc -l`
if [ $nameServerStatus -ge 1 ];
then
	echo "RocketMQ Name Server Start Sucess"
else
	echo "RocketMQ Name Server Start failed"
fi

################################################
#  Start RocketMQ Step 2
#      Start Broker with broker.properties
################################################
###Start Broker of RocketMQ with broker.properties
nohup sh /root/Coin/apache-rocketmq/bin/mqbroker -n 182.92.150.57:9876 -c /root/Coin/apache-rocketmq/broker.properties &
sleep 10s
brokerStatus=`cat /root/logs/rocketmqlogs/broker.log  |grep "boot success." |wc -l`
echo $brokerStatus
if [ $brokerStatus -ge 1 ];
then
	echo "RocketMQ broker Start Sucess"
else
	echo "RocketMQ broker Start failed"
fi

################################################
#  Start RocketMQ Step 3
#      Start rocketmq web-ui
################################################
nohup java -jar /root/Coin/rocketmq-externals/rocketmq-console/target/rocketmq-console-ng-1.0.0.jar --server.port=12581 --rocketmq.config.namesrvAddr=182.92.150.57:9876 >/root/logs/rocketmq-console.log &
export NAMESRV_ADDR=182.92.150.57:9876
sleep 10s

################################################
#  Start ZooKeeper
################################################
sh /root/Coin/zookeeper/bin/zkServer.sh restart
sleep 10s

################################################
#  Start Jstorm Step 0
#      Shutdown and cleanup
################################################
rm -rf /root/Coin/jstorm/logs/*

################################################
#  Start Jstorm Step 1
#      Start nimbus 
################################################
nohup jstorm nimbus &
sleep 10s

################################################
#  Start Jstorm Step 2
#      Start supervisor 
################################################
nohup jstorm supervisor &
sleep 10s

################################################
#  Start Jstorm Step 3
#      Start jstorm web-ui 
################################################
nohup sh /root/Coin/apache-tomcat-7.0.82/bin/catalina.sh start & 
sleep 10s

################################################
#  Start InfluxDB
################################################
sudo service influxdb restart
sleep 10s

################################################
#  Start Grafana
################################################
sudo service grafana-server restart
sleep 10s