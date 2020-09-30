package com.zhiy.zhiyes7service;

import org.elasticsearch.action.search.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilders;

//import org.springframework.data.elasticsearch.core.SearchHit;
//import org.springframework.data.elasticsearch.core.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchHit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ZhiyEs7ServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void locationQuery() throws UnknownHostException {
        //获取settings
        //配置es集群的名字
        Settings settings = Settings.builder().put("cluster.name", "lewk-application").build();
        //获取client客户端
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress
                (new TransportAddress(InetAddress.getByName("localhost"), 9300));
        /**
         * 基于矩形范围的数据搜索
         *  40.0519526142,116.4178513254
         *  40.0385828363,116.4465266673
         */
        //构建查询提交
        SearchResponse searchResponse = client.prepareSearch("car_shop")
                //.setTypes("store")
                .setQuery(QueryBuilders.geoBoundingBoxQuery("location")   //矩形
                        .setCorners(40.0519526142, 116.4178513254, 40.0385828363, 116.4465266673))
                .get();
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }

        System.out.println("====================================");

        client.close();
    }

}
