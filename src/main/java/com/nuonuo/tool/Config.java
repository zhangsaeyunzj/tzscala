package com.nuonuo.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2016/4/26.
 */
public class Config {
	private static final Logger logger = LogManager.getLogger(Config.class
			.getName());

	private static final String CLUSTER_NAME = "elasticsearch";
	private static final String CLIENT_TRANSPORT_SNIFF = "true";

	private static final String DRIVER_NAME = "org.apache.phoenix.queryserver.client.Driver";
	private static final String DB_URL = "jdbc:phoenix:thin:url=http://127.0.0.1:8765";
	public  static final String MOON = "R1cAUtcAVzBwDHgN";

	protected static String clusterName;
	protected static boolean isClientTransportSniff;
	protected static String socketAddress;
	protected static String[] socketAddresses;
	protected static List<Map<String, String>> socketList = new ArrayList<Map<String, String>>();;
	protected static String driverName;
	protected static String dbUrl;
	protected static String sqoopCommand;
	protected static String hbase_zookeeper_quorum;
	protected static String hbase_zookeeper_property_clientPort;

	private static final Properties prop = new Properties();

	static {
		try {
			logger.info("======开始加载config配置文件======");
			prop.load(Config.class.getResourceAsStream("/client.properties"));
			clusterName = prop.getProperty("elasticsearch.cluster.name",
					CLUSTER_NAME);
			isClientTransportSniff = Boolean.valueOf(prop.getProperty(
					"elasticsearch.client.transport.sniff",
					CLIENT_TRANSPORT_SNIFF));
			socketAddress = prop
					.getProperty("elasticsearch.node.socketAddress");
			socketAddresses = socketAddress.split(";");
			for (String socket : socketAddresses) {
				String[] array = socket.split(":");
				Map<String, String> map = new HashMap<String, String>();
				map.put("ip", array[0]);
				map.put("port", array[1]);
				socketList.add(map);
			}
			driverName = prop.getProperty("phoenix.driver.name", DRIVER_NAME);
			dbUrl = prop.getProperty("phoenix.url", DB_URL);
			sqoopCommand = prop.getProperty("sqoopCommond");
			hbase_zookeeper_quorum = prop.getProperty("hbase.zookeeper.quorum");
			hbase_zookeeper_property_clientPort = prop.getProperty("hbase.zookeeper.property.clientPort");
			logger.info("=====完成加载config配置文件======");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("加载配置文件出现异常", e);
		}
	}
}
