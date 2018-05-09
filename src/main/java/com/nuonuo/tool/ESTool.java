package com.nuonuo.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;


/**
 * Notice that elasticsearch client we use should be Singleton.
 * Getting client only can be from ESTool.getClient() method.
 * Created by zhangbingling on 2016/4/21.
 */
public class ESTool extends Config {

    private static final Logger logger = LogManager.getLogger(ESTool.class.getName());

    private static Settings settings;
    private static TransportClient instance;

    static {
        settings = Settings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", isClientTransportSniff).build();
        createClient();
        logger.info("===============加载esclient完成=================");
    }

    /**
     * Create an ElasticSearch Client
     */
    private static synchronized void createClient() {
        if (null != instance){//避免内存泄漏
            return;
        }
        instance = TransportClient.builder().settings(settings).build();
        try {
            for (Map<String, String> map :
                    socketList) {
                instance.addTransportAddress(
                        new InetSocketTransportAddress(InetAddress.getByName(map.get("ip")),
                                Integer.valueOf(map.get("port"))));
            }
        } catch (UnknownHostException e) {
            logger.error("创建esclient失败",e);
            e.printStackTrace();
        }
    }

    public static TransportClient getClient() {
        if (instance == null) {
            createClient();
        }
        return instance;
    }

    public static void closeClient() {
        instance.close();
    }

    public static void createIndex(String name) {
        logger.info("ES：新建" + name + "索引");
        getClient().admin().indices().prepareCreate(name).get();
    }

    public static void deleteIndex(String name){
        logger.info("ES：删除" + name + "索引");
        getClient().admin().indices().prepareDelete(name).execute().actionGet();
    }
    
    /**
     * create index with specified index name and type name 
     * @param index
     * @param type
     * @return new index name
     * @throws IOException
     */
    public static String createIndexForUserCode(final String index, final String type) throws IOException {
        final String newIndex = getNewIndexNameOnly(index);
        deleteIndex(newIndex);
        
		XContentBuilder settingsBuilder = XContentFactory.jsonBuilder()
				.startObject()
					.startObject("index")
						.field("number_of_shards", 5)
						.field("number_of_replicas", 1)
						.field("refresh_interval", "5s")
						.startObject("similarity")
							.startObject("PreciseTFIDF")
								.field("type", "PreciseTFIDF")
							.endObject()
						.endObject()
						.startObject("query")
							.field("default_field", "u_name")
						.endObject()
					.endObject()
				.endObject();
		
        XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
                .startObject()
					.startObject("_all")
						.field("enabled", false)
					.endObject()
					.field("include_in_all", false)
					
                    .startObject("properties")
                        .startObject("u_name")
                            .field("type", "string")
                            .field("store", "no")
                            .field("analyzer", "ik_max_word")
                            .field("search_analyzer", "ik_max_word")
        	          		.field("similarity", "PreciseTFIDF")
                            .field("include_in_all", "false")
                            .field("boost", "8")
                        .endObject()
                        .startObject("u_type")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_category")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_rate")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_code")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_choice_count")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_choice_percent")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_flag")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                    .endObject()
                .endObject();
        
        logger.info("ES：新建" + newIndex + "索引");
        final Client client = getClient();
		client.admin().indices().prepareCreate(newIndex).setSettings(settingsBuilder).addMapping(type, mappingBuilder).get();
		
		return newIndex;
    }
    
    public static void createMappingForUserCode(String indexName,String typeName) throws IOException {
        Client client = getClient(); 
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("u_name")
                            .field("type", "string")
                            .field("store", "no")
                            .field("analyzer", "ik_max_word")
                            .field("search_analyzer", "ik_max_word")
                            .field("include_in_all", "false")
                            .field("boost", "8")
                        .endObject()
                        .startObject("u_type")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_category")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_rate")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_code")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_choice_count")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_choice_percent")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                        .startObject("u_flag")
                            .field("type", "string")
                            .field("store", "no")
                            .field("index", "not_analyzed")
                        .endObject()
                    .endObject()
                .endObject();
        //再设置mapping
        client.admin().indices().preparePutMapping(indexName).setType(typeName)
                .setSource(mapping.string()).get();
        
    }

    public static void createMappingForProduct(String indexName,String typeName) throws IOException {
        Client client = getClient();
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("u_name")
                            .field("type", "string")
                            .field("store", "no")
                            .field("term_vector", "with_positions_offsets")
                            .field("analyzer", "ik_max_word")
                            .field("search_analyzer", "ik_max_word")
                            .field("include_in_all", "true")
                            .field("boost", "8")
                        .endObject()
                    .endObject()
                .endObject();
        //再设置mapping
        client.admin().indices().preparePutMapping(indexName).setType(typeName)
                .setSource(mapping.string()).get();
    }

    public static void updateAlias(String alias,String oldIndex,String newIndex){
        logger.info("ES:将" + alias + "别名指向"  + newIndex);
        try{
            getClient().admin().indices().prepareAliases().addAlias(newIndex,alias)
                    .removeAlias(oldIndex,alias).execute().actionGet();
        } catch (Exception e){
            logger.error(e);
            getClient().admin().indices().prepareAliases().addAlias(newIndex,alias).execute().actionGet();
        }
    }

    /**
     * 查询将别名设置为xxx的index
     * @param name
     * @return
     */
    public static String getAlias(final String name){
        String alias = null;
        GetAliasesResponse response = getClient().admin().indices().getAliases(new GetAliasesRequest(name)).actionGet();
        ImmutableOpenMap<String, List<AliasMetaData>> indexToAliasesMap = response.getAliases();
        if(indexToAliasesMap != null && !indexToAliasesMap.isEmpty()){
            alias = indexToAliasesMap.keys().iterator().next().value;
        }
        return alias;
    }

    /**
     * 只是得到新的Index
     * 会在1和2之间进行切换
     * @param name 别名名称
     * @return
     */
    public static String getNewIndexNameOnly(String name){
        String oldIndex = getAlias(name);
        String newIndex = null;
        if (null != oldIndex && oldIndex.indexOf("1")!= -1){
            newIndex = name + "2";
        } else {
            newIndex = name + "1";
        }
        return newIndex;
    }

    /**
     * 得到新的Index
     * 会在1和2之间进行切换,并建立新的索引及映射
     * @param name 别名名称
     * @return
     * @throws IOException
     */
    public static String getNewIndex(String name) throws IOException {
        String newIndex = getNewIndexNameOnly(name);
        try{
            deleteIndex(newIndex);
        } catch (Exception e){
            //do nothing
        }
        createIndex(newIndex);
        if(name.equals("user")){
            createMappingForUserCode(newIndex,"user_code");
        } else if (name.equals("product")){
            createMappingForProduct(newIndex,"name");
        }
        return newIndex;
    }

    /**
     * es索引热切换
     * @param name 别名名称
     */
    public static void updateAlias(String name){
        String oldIndex = getAlias(name);
        String newIndex = getNewIndexNameOnly(name);
        updateAlias(name,oldIndex,newIndex);
    }

    /**
     * 获取bulk进程
     * @param client ES Client
     * @return
     */
    public static BulkProcessor getBulkProcessor(Client client) {
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                    }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();
        return bulkProcessor;
    }
}
