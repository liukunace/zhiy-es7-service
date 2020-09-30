package com.zhiy.zhiyes7service.main;

import com.vividsolutions.jts.io.ParseException;
import com.zhiy.zhiyes7service.utils.WKTUtil;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-29 20:54
 */
public class WktTest {
    private static String pointstr="POINT (30 10)";
    private static String polystr="POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))";
    private static String linestr="LINESTRING (30 10, 10 30, 40 40)";
    private static String mpolystr="MULTIPOLYGON (((30 20, 45 40, 10 40, 30 20)))";

    public static void main(String[] args) throws ParseException {
        String aa1=WKTUtil.getESGeoTextFromWkt(polystr); //[[[30.0,10.0],[40.0,40.0],[20.0,40.0],[10.0,20.0],[30.0,10.0]]]
        System.out.println(aa1); //
    }



    /*
    POINT(6 10)
    LINESTRING(3 4,10 50,20 25)
 POLYGON((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2))
 MULTIPOINT(3.5 5.6, 4.8 10.5)
 MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))
 MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2,2 3,3 3,3 2,2 2)),((6 3,9 2,9 4,6 3)))
 GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))
 POINT ZM (1 1 5 60)
 POINT M (1 1 80)
 POINT EMPTY
 MULTIPOLYGON EMPTY
     */
}
