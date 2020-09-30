package com.zhiy.zhiyes7service.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-29 15:14
 */
public class ESUtil {

    /**
     * 获取mapping
     * @param map
     * @param id
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static XContentBuilder getMapping(Map<String, Object> map, String id) throws IOException, ParseException {
        // 字段mapping
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject();

        mapping.field("id", id);
        // 几何类型
        if (map.get("wkt") != null) {
            String polygon = map.get("wkt").toString();
            mapping.field("wkt", getConversionData(polygon));
        }
        // 循环添加字段和值
        for (String key : map.keySet()) {
            // 排除要不需要的字段
            if (key.equals("indexName") || key.equals("id") || key.equals("wkt")) {
                continue;
            }
            mapping.field(key, map.get(key));
        }
        mapping.endObject();
        return mapping;
    }

    /**
     * 获取json数据
     * @param map
     * @param id
     * @return
     * @throws ParseException
     */
    public static String getJsonData(Map<String, Object> map, String id) throws ParseException {
        String json = "{";
        json += "\"id\":\"" + id + "\",";
        if (map.get("id") != null) {
            map.remove("id");
        }
        if (map.get("wkt") != null) {
            // wkt数据
            String wkt = map.get("wkt").toString();
            // wkt类型
            String geometry = wkt.substring(0, wkt.indexOf("(")).trim().toLowerCase();
            //ES能存储的格式
            String data = "{\"coordinates\":";
            data += WKTUtil.getESGeoTextFromWkt(wkt) + ",\"type\":\"" + geometry + "\"},";
            json += "\"wkt\":" + data;
            map.remove("wkt");
        }
        int i = 1;
        for (String key : map.keySet()) {
            json += "\"" + key + "\":\"" + map.get(key).toString() + "\"";
            if (i != map.size()) {
                json += ",";
            }
            i++;
        }
        json += "}";
        return json;
    }

    /**
     * 获取转换数据
     * @param wkt
     * @return
     * @throws ParseException
     */
    private static Map<String,Object> getConversionData(String wkt) throws ParseException {
        //空间数据类型
        String type=wkt.substring(0,wkt.indexOf("(")).trim().toLowerCase();

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("type",type);
        WKTReader wktReader = new WKTReader();
        if(type.equals("point")){
            Point geoPoint =(Point) wktReader.read(wkt);
            Coordinate coordinates = geoPoint.getCoordinate();
            double[] point=new double[2];
            point[0]=coordinates.x;
            point[1]=coordinates.y;
            map.put("coordinates",point);
        }else if(type.equals("linestring")) {
            LineString geoLineString =(LineString) wktReader.read(wkt);
            Coordinate[] coordinates = geoLineString.getCoordinates();
            double[][] lineString = new double[coordinates.length][2];
            for(int i=0;i<coordinates.length;i++) {
                lineString[i][0] = coordinates[i].x;
                lineString[i][1] = coordinates[i].y;
            }
            map.put("coordinates",lineString);
        }else if(type.equals("polygon")||type.equals("multilinestring")) {
			/*Geometry geometry = wktReader.read(wkt);
			List<double[][]> list=new ArrayList<>();
			int length = geometry.getNumGeometries();
			for(int i=0;i<length;i++) {
				Geometry geometryN = geometry.getGeometryN(i);
				Coordinate[] coordinates = geometryN.getCoordinates();
				double[][] position = new double[coordinates.length][2];
				for(int j=0;j<coordinates.length;j++) {
					position[j][0]=coordinates[j].x;
					position[j][1]=coordinates[j].y;
				}
				list.add(position);
			}
			map.put("coordinates",list);*/
            //数据
            String data=wkt.trim().substring(wkt.indexOf("(")+1).trim();
            data=data.substring(0,data.length()-1).trim();
            String[] split = data.split("\\)");
            List<double[][]> list=new ArrayList<>();
            for(int i=0;i<split.length;i++) {
                String trim = split[i].replace("(", "").replace(")", "").trim();
                if((trim.charAt(0)+"").equals(",")) {
                    trim=trim.substring(trim.indexOf(",")+1);
                }
                String[] split2 = trim.split(",");
                double[][] position = new double[split2.length][2];
                for(int j=0;j<split2.length;j++) {
                    String ptstr = split2[j].trim();
                    String[] lon_lat = ptstr.split(" ");
                    position[j][0] = Double.parseDouble(lon_lat[0]);
                    position[j][1] = Double.parseDouble(lon_lat[1]);
                }
                list.add(position);
            }
            map.put("coordinates",list);
        }else if(type.equals("multipolygon")){
            map=null;
        }else{
            map=null;
        }
        return map;
    }

    /**
     * 创建索引库
     * @param client 客户端
     * @param idnexName 索引名称
     * @param type 类型
     * @return
     * @throws Exception
     */
    public static boolean createIndex(TransportClient client,String idnexName, String type) throws Exception {
        boolean sign=false;
        try {
            // 创建mapping
            XContentBuilder mapping = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject("properties")
                    .startObject("id").field("type", "text").endObject()
                    .startObject("wkt").field("type", "geo_shape").endObject().endObject()
                    .endObject();

            AdminClient admin = client.admin();
            IndicesAdminClient indicesAdminClient = admin.indices();
            CreateIndexResponse createIndexResponse = indicesAdminClient.prepareCreate(idnexName)
                    .setSettings(Settings.builder()
                            .put("index.number_of_shards", 5)   // 分片
                            .put("index.number_of_replicas", 1) // 副本
                    )
                    .addMapping(type, mapping) // mapping
                    .get();
            createIndexResponse.isAcknowledged();
            sign=true;
        } catch (Exception e) {
            e.getMessage();
        }
        return sign;
    }

    /**
     * 判断索引名称是否存在
     * @param client 客户端
     * @param indexName 索引名称
     * @return
     */
    public static boolean isExistsIndex(TransportClient client,String indexName){
        IndicesExistsResponse  response = client.admin().indices()
                .exists(new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
        return response.isExists();
    }

}
