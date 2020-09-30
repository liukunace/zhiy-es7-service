package com.zhiy.zhiyes7service.utils;


import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
//import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-29 15:01
 */
public class WKTUtil {

    //private final static WKTReader wktReader = new WKTReader(JTSFactoryFinder.getGeometryFactory());

    private final static  GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
    private final static  WKTReader wktReader = new WKTReader(geometryFactory);

    /**
     * 将WKT文本字符串转换为ES中的GeoShape字符串格式
     * @param wkt
     * @return [[[30.0,10.0],[40.0,40.0],[20.0,40.0],[10.0,20.0],[30.0,10.0]]]
     * @throws ParseException
     */
    public static String getESGeoTextFromWkt(String wkt) throws ParseException {
        String result = null;
        Geometry geom = wktReader.read(wkt);
        String type = geom.getClass().getSimpleName().toLowerCase();
        Point point;
        MultiPoint multiPoint;
        LineString lineString;
        MultiLineString multiLineString;
        Polygon polygon;
        MultiPolygon multiPolygon;
        if(type.equals("point")){
            point = (Point) geom;
            result = getESPointText(point);
        }else if(type.equals("linestring")){
            lineString = (LineString) geom;
            result = getESLineStringText(lineString);
        }else if(type.equals("polygon")){
            polygon = (Polygon) geom;
            result = getESPolygonText(polygon);
        }else if(type.equals("multipoint")){
            multiPoint = (MultiPoint) geom;
            result = getESMultiPointText(multiPoint);
        }else if(type.equals("multilinestring")){
            multiLineString = (MultiLineString) geom;
            result = getESMultiLineStringText(multiLineString);
        }else if(type.equals("multipolygon")){
            multiPolygon = (MultiPolygon) geom;
            result = getESMultiPolygonText(multiPolygon);
        }
        return result;
    }

    /**
     * 通过MultiPolygon对象拼接中括号表示的字符串
     * @param multiPolygon
     * @return
     */
    private static String getESMultiPolygonText(MultiPolygon multiPolygon) throws ParseException {
        Polygon polygon;
        int num = multiPolygon.getNumGeometries();
        StringBuffer sb = new StringBuffer("[");
        for(int i = 0; i < num; i++){
            polygon = (Polygon) multiPolygon.getGeometryN(i);
            sb.append(getESPolygonText(polygon)+",");
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    /**
     * 通过MultiLineString对象拼接中括号表示的字符串
     * @param multiLineString
     * @return
     */
    private static String getESMultiLineStringText(MultiLineString multiLineString) {
        LineString lineString;
        int num = multiLineString.getNumGeometries();
        StringBuffer sb = new StringBuffer("[");
        for(int i = 0; i < num; i++){
            lineString = (LineString) multiLineString.getGeometryN(i);
            sb.append(getESLineStringText(lineString)+",");
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    /**
     * 通过MultiPoint对象拼接中括号表示的字符串
     * @param multiPoint
     * @return
     */
    private static String getESMultiPointText(MultiPoint multiPoint) {
        Point point;
        int num = multiPoint.getNumGeometries();
        StringBuffer sb = new StringBuffer("[");
        for(int i = 0; i < num; i++){
            point = (Point) multiPoint.getGeometryN(i);
            sb.append(getESPointText(point)+",");
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    /**
     * 通过Polygon对象拼接中括号表示的字符串
     * @param polygon
     * @return
     * @throws ParseException
     */
    private static String getESPolygonText(Polygon polygon){
        String result;
        /**
         * 内部闭合环形的数量
         */
        int num = polygon.getNumInteriorRing();
        if(num > 0){
            StringBuffer sb = new StringBuffer("["+getESLineStringText(polygon.getExteriorRing())+",");
            for(int i = 0; i < num; i++){
                sb.append(getESLineStringText(polygon.getInteriorRingN(i))+",");
            }
            sb.setCharAt(sb.length() - 1, ']');
            result = sb.toString();
        }else{
            result = "["+getESLineStringText(polygon.getExteriorRing())+"]";
        }
        return result;
    }

    /**
     * 通过LineString对象拼接中括号表示的字符串
     * @param lineString
     * @return
     */
    public static String getESLineStringText(LineString lineString){
        Coordinate[] corrds = lineString.getCoordinates();
        StringBuffer sb = new StringBuffer("[");
        for(Coordinate corrd : corrds) {
            sb.append(getCoordinateText(corrd)+",");
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    /**
     * 通过Point对象拼接中括号表示的字符串
     * @param point
     * @return
     */
    public static String getESPointText(Point point){
        return getCoordinateText(point.getCoordinate());
    }

    /**
     * 通过Coordinate对象拼接中括号表示的字符串
     * @param coord
     * @return
     */
    public static String getCoordinateText(Coordinate coord) {
        return "["+coord.x+","+coord.y+"]";
    }

}
