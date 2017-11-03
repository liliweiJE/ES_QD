package com.ep.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchUtil {
	
	public static Logger logger = LoggerFactory.getLogger(ElasticSearchUtil.class);

	public static final String HOST = "192.168.139.106";
	
	public static final int PORT = 9300;
	
	public static final String CLUSTER_NAME = "my-es";
	
	public static final String CLUSTER_Nodes = "192.168.139.106:9300";
	
	private static TransportClient client;
	
	//private static boolean isCliented = false;
	
	//单例初始化client
	static{
		logger.info("start init elasticsearch client...");
		try {
			Settings settings = Settings.builder()
		            .put("cluster.name",CLUSTER_NAME) 
		            .put("client.transport.sniff",true).build();
            PreBuiltTransportClient  preBuiltTransportClient = new PreBuiltTransportClient(settings);
            if (!"".equals(CLUSTER_Nodes)){
                for (String nodes:CLUSTER_Nodes.split(",")) {
                    String InetSocket [] = nodes.split(":");
                    String  Address = InetSocket[0];
                    Integer  port = Integer.valueOf(InetSocket[1]);
                    preBuiltTransportClient.addTransportAddress(new
                                     InetSocketTransportAddress(InetAddress.getByName(Address),port ));
                }
                client = preBuiltTransportClient;
            }
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
	}
	
	
	public static TransportClient getElasticSearchClient(){
		return client;
	}
	
	public static void closeClient(TransportClient  client){
		client.close();
	}
}
