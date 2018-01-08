package com.zdx.test;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

public class TestInfluxDB {
	private static Logger logger = Logger.getLogger(TestInfluxDB.class);
	public static void main(String[] args) {
		testCase1();
	}

	public static void testCase1(){
		logger.debug("-------------1-------------");
		InfluxDB influxDB = InfluxDBFactory.connect("http://49.51.37.51:8086");
		String dbName = "test1";
		logger.debug("-------------2-------------");
		influxDB.createDatabase(dbName);
		String rpName = "aRetentionPolicy";
		influxDB.createRetentionPolicy(rpName, dbName, "30d", "30m", 2, true);

		BatchPoints batchPoints = BatchPoints
				.database(dbName)
				.tag("async", "true")
				.retentionPolicy(rpName)
				.consistency(ConsistencyLevel.ALL)
				.build();
		Point point1 = Point.measurement("cpu")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("idle", 90L)
				.addField("user", 9L)
				.addField("system", 1L)
				.build();
		Point point2 = Point.measurement("disk")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("used", 80L)
				.addField("free", 1L)
				.build();
		batchPoints.point(point1);
		batchPoints.point(point2);
		influxDB.write(batchPoints);
		Query query = new Query("SELECT idle FROM cpu", dbName);
		influxDB.query(query);
		//influxDB.dropRetentionPolicy(rpName, dbName);
		//influxDB.deleteDatabase(dbName);
	}
}
