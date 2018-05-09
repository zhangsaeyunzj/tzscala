package com.nuonuo.tool;


import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.sql.*;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to import data into es from hbase.
 * Coding...
 * Created by zhangbingling on 2016/4/29.
 */
public class HbaseToES extends Config {
    static {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    //从hbase导入es
    public static void importToES() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        Client client = ESTool.getClient();
        BulkProcessor processor = getBulkProcessor(client);
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            String sql = "select \"pk\",\"action\",\"salestaxname\" from \"NNZhKp_log\"";
            long time1 = System.currentTimeMillis();
            rs = stmt.executeQuery(sql);
            long time2 = System.currentTimeMillis();
            while (rs.next()) {
//                System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
                processor.add(new IndexRequest("hbasephoenix", "log").
                        source(XContentFactory.jsonBuilder().startObject()
                                .field("pk", rs.getString(1))
                                .field("action", rs.getString(2))
                                .field("salertaxname", rs.getString(3))
                                .endObject()));
            }
            processor.awaitClose(100, TimeUnit.MINUTES);
            long time3 = System.currentTimeMillis();
            System.out.println(time3 - time2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            processor.close();
            ESTool.closeClient();
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != stmt) {
                    stmt.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

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
