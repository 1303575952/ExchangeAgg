package com.zdx.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zdx.producer.TickerProducerConf;

public class LoadConfig {
	private static Logger logger = Logger.getLogger(LoadConfig.class);

	
	private static HashMap<Object, Object> loadProperty(String prop) {
		HashMap<Object, Object> ret = null;
		Properties properties = new Properties();

		try {
			InputStream stream = new FileInputStream(prop);
			properties.load(stream);
			ret = new HashMap<Object, Object>();
			ret.putAll(properties);
		} catch (FileNotFoundException e) {
			logger.info("No such file " + prop);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private static HashMap<Object, Object> loadYaml(String confPath) {
		HashMap<Object, Object> ret = null;
		Yaml yaml = new Yaml();
		try {
			InputStream stream = new FileInputStream(confPath);

			ret = (HashMap<Object, Object>) yaml.load(stream);
			if (ret == null || ret.isEmpty() == true) {
				throw new RuntimeException("Failed to read config file");
			}

		} catch (FileNotFoundException e) {
			logger.info("No such file " + confPath);
			throw new RuntimeException("No config file");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("Failed to read config file");
		}

		return ret;
	}

	public static HashMap<Object, Object> loadConf(String arg) {
		HashMap<Object, Object> ret = null;

		if (arg.endsWith(CommonConst.YAML)) {
			ret = loadYaml(arg);
		} else {
			ret = loadProperty(arg);
		}
		return ret;
	}
}

