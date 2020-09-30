package com.zhiy.zhiyes7service.main;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
//import com.vividsolutions.jts.geom.Coordinate;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.geometry.Point;
import org.elasticsearch.geometry.Line;
import org.elasticsearch.geometry.Polygon;
import org.elasticsearch.common.geo.builders.*;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.geometry.GeometryVisitor;
import org.elasticsearch.geometry.Polygon;
import org.elasticsearch.geometry.ShapeType;
import org.elasticsearch.geometry.LinearRing;
import org.elasticsearch.index.query.*;
import org.locationtech.jts.geom.Coordinate;
import org.elasticsearch.action.ListenableActionFuture;

import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.common.geo.ShapeRelation;

//import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.locationtech.jts.geom.Geometry;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 10:30
 */
public class GeoLocationShopSearchApp {

    private static TransportClient client;

    private void pretest(){
        //7.x
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(new InetSocketAddress(InetAddresses.forString("127.0.0.1"), 9300)));

        String query = "{\"term\": {\"user\": \"kimchy\"}}";
        //wrapperQuery(query);
    }

    public static void testBefore() throws UnknownHostException {
        /*  老版本
        Settings settings = Settings.settingsBuilder().put("cluster.name", "wenbronk_escluster").build();
        TransportClient client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("192.168.50.37", 9300)));
        System.out.println("success to connect escluster");
         */
        //获取settings
        //配置es集群的名字
        Settings settings = Settings.builder().put("cluster.name", "lewk-application").build();
        //获取client客户端
         client = new PreBuiltTransportClient(settings).addTransportAddress
                (new TransportAddress(InetAddress.getByName("localhost"), 9300));
        System.out.println("success to connect escluster");
    }

    public static void main(String[] args) throws IOException, ParseException {
        testBefore();
        GeoLocationShopSearchApp gs=new GeoLocationShopSearchApp();
        //gs.testPrefixQuery();//ok
        //gs.testGet();//ok
        //gs.testQueryBuilder();
        //gs.testQueryBuilder2();//null
        //gs.testIdsQuery();//ok
        //gs.shapeq2();
        //gs.shapeq3();//error  [field [location] is not a geo_point field]
        gs.shapeq5();

    }

    Coordinate coordinate0 = new Coordinate(109.0, 0.0);
    Coordinate coordinate1 = new Coordinate(110.0, 0.0);
    Coordinate coordinate2 = new Coordinate(110.0, 5.0);
    Coordinate coordinate3 = new Coordinate(109.0, 5.0);
    Coordinate coordinate4 = new Coordinate(109.0, 0.0);

    public void shapeq5() throws IOException, ParseException {
        ShapeBuilder circleBuilder = new CircleBuilder().center(100.0, 0.0).radius(100, DistanceUnit.KILOMETERS);

        ShapeBuilder polygonBuilder = new PolygonBuilder(new CoordinatesBuilder()
                .coordinate(coordinate0)
                .coordinate(coordinate1)
                .coordinate(coordinate2)
                .coordinate(coordinate3).close());
        //org.elasticsearch.geometry.Geometry
        new GeoPoint(13.4080, 52.5186);

        new Point(13.4080, 52.5186);

        Line line=new Line(new double[]{109.0, 110.0,110.0,109.0,109.0},
                new double[]{0.0, 0.0,5.0,5.0,0.0} );   //double[]

        LinearRing linearRing=new LinearRing(
                new double[]{109.0, 110.0,110.0,109.0,109.0},
                new double[]{0.0, 0.0,5.0,5.0,0.0} );

        Polygon polygon=new Polygon(linearRing);

        // Geometry g=

        GeoShapeQueryBuilder qb3 = QueryBuilders.geoShapeQuery("location",polygon).relation(ShapeRelation.INTERSECTS);

        SearchRequestBuilder requestBuilder = client.prepareSearch("example090505");//.setTypes("_doc");
        // 声明where条件
        BoolQueryBuilder qbs = QueryBuilders.boolQuery();

        qbs.must(qb3);

        requestBuilder.setQuery(qbs);

        SearchResponse response = requestBuilder.setFrom(0).setSize(3).execute().actionGet();

        if (response != null && response.getHits() != null && response.getHits().getHits()!=null) {

            final ArrayList<SearchHit> searchHits = Lists.newArrayList(response.getHits().getHits());
            searchHits.stream().forEach(searchHit -> printHitsSource(searchHit));
        }

    }

    public void shapeq3() throws IOException, ParseException {
        ShapeBuilder polygonBuilder = new PolygonBuilder(new CoordinatesBuilder()
                .coordinate(coordinate0)
                .coordinate(coordinate1)
                .coordinate(coordinate2)
                .coordinate(coordinate3).close());
        ///GeoShapeQueryBuilder qb3=QueryBuilders.geoShapeQuery("location",polygonBuilder);

        List<GeoPoint> list=new ArrayList<>();
        list.add(new GeoPoint(coordinate0.y,coordinate0.x));
        list.add(new GeoPoint(coordinate1.y,coordinate1.x));
        list.add(new GeoPoint(coordinate2.y,coordinate2.x));
        list.add(new GeoPoint(coordinate3.y,coordinate3.x));
        list.add(new GeoPoint(coordinate4.y,coordinate4.x));
        GeoPolygonQueryBuilder qb3 = QueryBuilders.geoPolygonQuery("location",list);
        //[field [location] is not a geo_point field]

         //qb3.relation(ShapeRelation.INTERSECTS);

        SearchRequestBuilder requestBuilder = client.prepareSearch("example090505");//.setTypes("_search");
        // 声明where条件
        BoolQueryBuilder qbs = QueryBuilders.boolQuery();

        qbs.must(qb3);

        requestBuilder.setQuery(qbs);

        SearchResponse response = requestBuilder.setFrom(0).setSize(3).execute().actionGet();

        if (response != null && response.getHits() != null && response.getHits().getHits()!=null) {
            final ArrayList<SearchHit> searchHits = Lists.newArrayList(response.getHits().getHits());
            searchHits.stream().forEach(searchHit -> printHitsSource(searchHit));
        }
    }

    //漂亮
    //{"name":"name10","location":{"coordinates":[[[100.0,0.0],[110.0,0.0],[110.0,5.0],[100.0,5.0],[100.0,0.0]]],"type":"polygon"},"id":"10"}
    public void shapeq2() throws IOException, ParseException {
        ShapeBuilder polygonBuilder = new PolygonBuilder(new CoordinatesBuilder()
                .coordinate(coordinate0)
                .coordinate(coordinate1)
                .coordinate(coordinate2)
                .coordinate(coordinate3).close());
        GeoShapeQueryBuilder qb3=QueryBuilders.geoShapeQuery("location",polygonBuilder);
        qb3.relation(ShapeRelation.INTERSECTS);

        SearchRequestBuilder requestBuilder = client.prepareSearch("example090505");//.setTypes("_doc");
        // 声明where条件
        BoolQueryBuilder qbs = QueryBuilders.boolQuery();

        qbs.must(qb3);

        requestBuilder.setQuery(qbs);

        SearchResponse response = requestBuilder.setFrom(0).setSize(2).execute().actionGet();

        if (response != null && response.getHits() != null && response.getHits().getHits()!=null) {

            final ArrayList<SearchHit> searchHits = Lists.newArrayList(response.getHits().getHits());
            searchHits.stream().forEach(searchHit -> printHitsSource(searchHit));
        }


    }
    public void shapeq() throws IOException, ParseException {

//[109.0, 0.0], [110.0, 0.0], [110.0, 5.0], [109.0, 5.0], [109.0, 0.0]

       // GeoShape

        GeoShapeQueryBuilder qb = QueryBuilders.geoShapeQuery(
                "location",
                new MultiPointBuilder( new CoordinatesBuilder()
                        .coordinate(coordinate0)
                        .coordinate(coordinate1)
                        .coordinate(coordinate2)
                        .coordinate(coordinate3)
                        .coordinate(coordinate4)
                        .build()));
        qb.relation(ShapeRelation.INTERSECTS);

        //点面，如电子围栏，机车（点输入）闯入电子围栏触发报警
        //ShapeBuilder pointBuilder = new PointBuilder(33, 111);
        //线面，新修公路（线输入）线路计划合理性计算，尽量与较多的村庄相交或内含
        //ShapeBuilder pointBuilder = new LineStringBuilder(new CoordinatesBuilder().coordinate(11,22).coordinate(33,44));// 点面位置关系
        //面面，如汶川多个村庄(在ES中表现为多个坐标组成的多边形)，地震震中（面输入）（x,y） r=100Km，计算波及的村庄
        ShapeBuilder circleBuilder = new CircleBuilder().center(100.0, 0.0).radius(100, DistanceUnit.KILOMETERS);

        ShapeBuilder polygonBuilder = new PolygonBuilder(new CoordinatesBuilder()
                .coordinate(coordinate0)
                .coordinate(coordinate1)
                .coordinate(coordinate2)
                .coordinate(coordinate3).close());


        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(coordinate0.y, coordinate0.x));

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        WKTReader wktReader = new WKTReader(geometryFactory);
        /**
         * Geometry对象，包含Point、LineString、Polygon等子类
         */
       //Geometry geometry = wktReader.read("POINT (113.53896635 22.36429837)");

        String pointStr="POLYGON((109.0 0.0, 110.0 0.0, 110.0 5.0, 109.0 5.0, 109.0 0.0))";
        //Geometry geometry = wktReader.read(pointStr);
        //GeoShapeQueryBuilder qb3 =QueryBuilders.geoShapeQuery("location", (org.elasticsearch.geometry.Geometry) geometry);

        GeoShapeQueryBuilder qb3 =QueryBuilders.geoShapeQuery("location",
                new PolygonBuilder(new CoordinatesBuilder()
                .coordinate(coordinate0)
                .coordinate(coordinate1)
                .coordinate(coordinate2)
                .coordinate(coordinate3)
                .coordinate(coordinate4).close()));

        qb3=QueryBuilders.geoShapeQuery("location",polygonBuilder);
        qb3.relation(ShapeRelation.INTERSECTS);

        SearchResponse response = client.prepareSearch("example090505")
                .setTypes("doc")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb3)
                .setSize(200)
                .setFrom(0)
                .execute()
                .actionGet();

        SearchRequestBuilder requestBuilder=client.prepareSearch("example090505")
                .setTypes("doc")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb3)
                ;

        if (response != null && response.getHits() != null && response.getHits().getHits()!=null) {

            final ArrayList<SearchHit> searchHits = Lists.newArrayList(response.getHits().getHits());
            searchHits.stream().forEach(searchHit -> printHitsSource(searchHit));
        }

        Arrays.stream(requestBuilder.get().getHits().getHits()).forEach(searchHit -> {
            Map<String, Object> source = searchHit.getSourceAsMap();
            System.out.println(source);
        });

        client.close();
    }

    public  void printHitsSource(SearchHit searchHitFields){
        Map<String, Object> source = searchHitFields.getSourceAsMap();
        System.out.println(new Gson().toJson(source));
    }

    public void testPrefixQuery() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "王五");
        searchFunction(queryBuilder);
    }

    /**
     * 使用QueryBuilder
     * termQuery("key", obj) 完全匹配
     * termsQuery("key", obj1, obj2..)   一次匹配多个值
     * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     * multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
     * matchAllQuery();         匹配所有文件
     */
    public void testQueryBuilder() {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("name", "酷狗");
        //QueryBUilder queryBuilder = QueryBuilders.termQuery("user", "kimchy", "wenbronk", "vini");
        //QueryBuilders.termsQuery("name", new ArrayList<String>().add("酷狗"));
//        QueryBuilder queryBuilder = QueryBuilders.matchQuery("user", "kimchy");
//        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("kimchy", "user", "message", "gender");
        //QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        searchFunction(queryBuilder);

    }




    private void geoSearchFunction(QueryBuilder queryBuilder,String idexName) {
        SearchResponse response = client.prepareSearch(idexName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setScroll(new TimeValue(20000))
                .setQuery(queryBuilder)//.get();
                .setSize(100).execute().actionGet();
        while(true) {
            for (SearchHit hit : response.getHits()) {
                hit.getSourceAsString();
                Map<String , Object> hmap=hit.getSourceAsMap();
                Iterator<Map.Entry<String , Object>> iterator=hmap.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry<String, Object> next = iterator.next();
                    System.out.println(next.getKey() + ": " + next.getValue());
                    if(response.getHits().getHits().length == 0) {
                        break;
                    }
                }
            }
            break;
        }
    }


    /**
     * 查询遍历抽取
     * @param queryBuilder
     */
    private void searchFunction(QueryBuilder queryBuilder) {
        SearchResponse response = client.prepareSearch("user_test")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setScroll(new TimeValue(20000))
                .setQuery(queryBuilder)//.get();
                .setSize(100).execute().actionGet();

        //构建查询提交
//        SearchResponse searchResponse = client.prepareSearch("car_shop")
//                //.setTypes("store")
//                .setQuery(QueryBuilders.geoBoundingBoxQuery("location")   //矩形
//                        .setCorners(40.0519526142, 116.4178513254, 40.0385828363, 116.4465266673))
//                .get();

        while(true) {
            //response = client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            for (SearchHit hit : response.getHits()) {
                hit.getSourceAsString();
                Map<String , Object> hmap=hit.getSourceAsMap();
                //Iterator<Map<String , Object>> iterator = hit.getSourceAsMap().iterator();//hit.getSource().entrySet().iterator();
                Iterator<Map.Entry<String , Object>> iterator=hmap.entrySet().iterator();

                while(iterator.hasNext()) {
                    Map.Entry<String, Object> next = iterator.next();
                    System.out.println(next.getKey() + ": " + next.getValue());
                    if(response.getHits().getHits().length == 0) {
                        break;
                    }
                }
            }
            break;
        }
//        testResponse(response);
    }

    /**
     * 使用get查询
     */
    public void testGet() {
        GetRequestBuilder requestBuilder = client.prepareGet("user_test", null, "2");
        GetResponse response = requestBuilder.execute().actionGet();
        GetResponse getResponse = requestBuilder.get();
        //PlainActionFuture<GetResponse> execute =  requestBuilder.execute();
        System.out.println(response.getSourceAsString());
    }



    /**
     * 组合查询
     * must(QueryBuilders) :   AND
     * mustNot(QueryBuilders): NOT
     * should:                  : OR
     */
    public void testQueryBuilder2() {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name", "酷狗"))
                //.mustNot(QueryBuilders.termQuery("message", "nihao"))
                .should(QueryBuilders.termQuery("tags", "喜欢"));
        searchFunction(queryBuilder);
    }

    /**
     * 只查询一个id的
     * QueryBuilders.idsQuery(String...type).ids(Collection<String> ids)
     */
    public void testIdsQuery() {
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1");
        searchFunction(queryBuilder);
    }

    /**
     * 包裹查询, 高于设定分数, 不计算相关性
     */
    public void testConstantScoreQuery() {
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name", "kimchy")).boost(2.0f);
        searchFunction(queryBuilder);
        // 过滤查询
//        QueryBuilders.constantScoreQuery(FilterBuilders.termQuery("name", "kimchy")).boost(2.0f);

    }

    /**
     * disMax查询
     * 对子查询的结果做union, score沿用子查询score的最大值,
     * 广泛用于muti-field查询
     */
    public void testDisMaxQuery() {
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.termQuery("user", "kimch"))  // 查询条件
                .add(QueryBuilders.termQuery("message", "hello"))
                .boost(1.3f)
                .tieBreaker(0.7f);
        searchFunction(queryBuilder);
    }

    /**
     * 模糊查询
     * 不能用通配符, 不知道干啥用
     */
    public void testFuzzyQuery() {
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("user", "kimch");
        searchFunction(queryBuilder);
    }

    /**
     * 父或子的文档查询
     */
    public void testChildQuery() {
        //QueryBuilder queryBuilder = QueryBuilders.hasChildQuery("sonDoc", QueryBuilders.termQuery("name", "vini"));

        //searchFunction(queryBuilder);
    }

    /**
     * moreLikeThisQuery: 实现基于内容推荐, 支持实现一句话相似文章查询
     * {
     "more_like_this" : {
     "fields" : ["title", "content"],   // 要匹配的字段, 不填默认_all
     "like_text" : "text like this one",   // 匹配的文本
     }
     }
     percent_terms_to_match：匹配项（term）的百分比，默认是0.3
     min_term_freq：一篇文档中一个词语至少出现次数，小于这个值的词将被忽略，默认是2
     max_query_terms：一条查询语句中允许最多查询词语的个数，默认是25
     stop_words：设置停止词，匹配时会忽略停止词
     min_doc_freq：一个词语最少在多少篇文档中出现，小于这个值的词会将被忽略，默认是无限制
     max_doc_freq：一个词语最多在多少篇文档中出现，大于这个值的词会将被忽略，默认是无限制
     min_word_len：最小的词语长度，默认是0
     max_word_len：最多的词语长度，默认无限制
     boost_terms：设置词语权重，默认是1
     boost：设置查询权重，默认是1
     analyzer：设置使用的分词器，默认是使用该字段指定的分词器
     */
    public void testMoreLikeThisQuery() {
        String str[]=new String[1];
        str[0]="name";
        QueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(str);
                //.like("kimchy");
//                            .minTermFreq(1)         //最少出现的次数
//                            .maxQueryTerms(12);     // 最多允许查询的词语
        searchFunction(queryBuilder);
    }

    /**
     * 前缀查询
     */

    public void testPrefixQuery1() {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("user", "kimchy");
        searchFunction(queryBuilder);
    }

    /**
     * 查询解析查询字符串
     */

    public void testQueryString() {
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("+kimchy");
        searchFunction(queryBuilder);
    }

    /**
     * 范围内查询
     */
    public void testRangeQuery() {
        QueryBuilder queryBuilder = QueryBuilders.rangeQuery("user")
                .from("kimchy")
                .to("wenbronk")
                .includeLower(true)     // 包含上界
                .includeUpper(true);      // 包含下届
        searchFunction(queryBuilder);
    }

    /**
     * 跨度查询
     */
    public void testSpanQueries() {
        /*
        QueryBuilder queryBuilder1 = QueryBuilders.spanFirstQuery(QueryBuilders.spanTermQuery("name", "葫芦580娃"), 30000);     // Max查询范围的结束位置

        QueryBuilder queryBuilder2 = QueryBuilders.spanNearQuery()
                .clause(QueryBuilders.spanTermQuery("name", "葫芦580娃")) // Span Term Queries
                .clause(QueryBuilders.spanTermQuery("name", "葫芦3812娃"))
                .clause(QueryBuilders.spanTermQuery("name", "葫芦7139娃"))
                .slop(30000)                                               // Slop factor
                .inOrder(false)
                .collectPayloads(false);

        // Span Not
        QueryBuilder queryBuilder3 = QueryBuilders.spanNotQuery()
                .include(QueryBuilders.spanTermQuery("name", "葫芦580娃"))
                .exclude(QueryBuilders.spanTermQuery("home", "山西省太原市2552街道"));

        // Span Or
        QueryBuilder queryBuilder4 = QueryBuilders.spanOrQuery()
                .clause(QueryBuilders.spanTermQuery("name", "葫芦580娃"))
                .clause(QueryBuilders.spanTermQuery("name", "葫芦3812娃"))
                .clause(QueryBuilders.spanTermQuery("name", "葫芦7139娃"));

        // Span Term
        QueryBuilder queryBuilder5 = QueryBuilders.spanTermQuery("name", "葫芦580娃");

         */
    }

    /**
     * 测试子查询
     */
    public void testTopChildrenQuery() {
       // QueryBuilders.hasChildQuery("tweet", QueryBuilders.termQuery("user", "kimchy")) .scoreMode("max");
    }



    /**
     * 通配符查询, 支持 *
     * 匹配任何字符序列, 包括空
     * 避免* 开始, 会检索大量内容造成效率缓慢
     */
    public void testWildCardQuery() {
        QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("user", "ki*hy");
        searchFunction(queryBuilder);
    }

    /**
     * 嵌套查询, 内嵌文档查询
     */
    public void testNestedQuery() {
        //QueryBuilder queryBuilder = QueryBuilders.nestedQuery("location",QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("location.lat", 0.962590433140581))
                       // .must(QueryBuilders.rangeQuery("location.lon").lt(36.0000).gt(0.000)))
               // .scoreMode("total");

    }

    /**
     * 测试索引查询
     */
    public void testIndicesQueryBuilder () {
       // QueryBuilder queryBuilder = QueryBuilders.indicesQuery(
             //   QueryBuilders.termQuery("user", "kimchy"), "index1", "index2")
             //   .noMatchQuery(QueryBuilders.termQuery("user", "kimchy"));

    }



    /**
     * 对response结果的分析
     * @param response
     */
    public void testResponse(SearchResponse response) {
        // 命中的记录数
        long totalHits = response.getHits().getHits().length;//.totalHits();

        for (SearchHit searchHit : response.getHits()) {
            // 打分
            float score = searchHit.getScore();
            // 文章id
            int id = Integer.parseInt(searchHit.getSourceAsMap().get("id").toString());
            // title
            String title = searchHit.getSourceAsMap().get("title").toString();
            // 内容
            String content = searchHit.getSourceAsMap().get("content").toString();
            // 文章更新时间
            long updatetime = Long.parseLong(searchHit.getSourceAsMap().get("updatetime").toString());
        }
    }

    /**
     * 对结果设置高亮显示
     */
    public void testHighLighted() {
        /*  5.0 版本后的高亮设置
         * client.#().#().highlighter(hBuilder).execute().actionGet();
        HighlightBuilder hBuilder = new HighlightBuilder();
        hBuilder.preTags("<h2>");
        hBuilder.postTags("</h2>");
        hBuilder.field("user");        // 设置高亮显示的字段
        */
        // 加入查询中
        SearchResponse response = client.prepareSearch("blog")
                .setQuery(QueryBuilders.matchAllQuery())
                //.addHighlightedField("user")        // 添加高亮的字段
                //.setHighlighterPreTags("<h1>")
                //.setHighlighterPostTags("</h1>")
                .execute().actionGet();

        // 遍历结果, 获取高亮片段
        SearchHits searchHits = response.getHits();
        for(SearchHit hit:searchHits){
            System.out.println("String方式打印文档搜索内容:");
            System.out.println(hit.getSourceAsString());
            System.out.println("Map方式打印高亮内容");
            System.out.println(hit.getHighlightFields());

            System.out.println("遍历高亮集合，打印高亮片段:");
            Text[] text = hit.getHighlightFields().get("title").getFragments();
            for (Text str : text) {
                System.out.println(str.string());
            }
        }
    }


    /*
    @SuppressWarnings({ "resource", "unchecked" })
    public static void main(String[] args) throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        Coordinate coordinate = new Coordinate(100.3, 0.3);
        GeoShapeQueryBuilder qb = null;
        try {
            qb = geoShapeQuery(
                    "0",
                    ShapeBuilders.newPoint(coordinate));
        } catch (IOException e) {
            e.printStackTrace();
        }
        qb.relation(ShapeRelation.INTERSECTS);
        SearchResponse response = client.prepareSearch("example")
                .setTypes("doc")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)
                .setSize(200)
                .setFrom(0)
                .execute()
                .actionGet();
        if (response != null && response.getHits() != null && response.getHits().getHits()!=null) {

            final ArrayList<SearchHit> searchHits = Lists.newArrayList(response.getHits().getHits());
            searchHits.stream().forEach(searchHit -> printHitsSource(searchHit));
        }
        client.close();
    }

    public static void printHitsSource(SearchHit searchHitFields){
        Map<String, Object> source = searchHitFields.getSource();
        System.out.println(new Gson().toJson(source));
    }

     */

}
