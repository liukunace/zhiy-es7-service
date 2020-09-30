package com.zhiy.zhiyes7service.rep;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-27 20:31
 */
import com.zhiy.zhiyes7service.bean.DocGeoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Iterator;
import java.util.List;

public interface GeoElasticRepository extends ElasticsearchRepository<DocGeoBean, Long> {

    //默认的注释
    @Query("{\"query\":{\"bool\":{\"must\":{\"match_all\":{}},\"filter\":{\"geo_shape\":{\"location\":{\"shape\":{\"type\":\"polygon\",\"coordinates\":[[[116.791130753538,36.3330038752429],[116.791287393228,36.3330319219673],[116.791441958473,36.333066668637],[116.791594010832,36.3331080166896],[116.791743118991,36.3331558488367],[116.791888859989,36.3332100293975],[116.792030820417,36.333270404683],[116.792037271667,36.3332534641137],[116.791130753538,36.3330038752429]]]},\"relation\":\"intersects\"}}}}}}")
    Page<DocGeoBean> findGeoTest(String pname, Pageable pageable);

    @Query("{\"bool\" : {\"must\" : {\"term\" : {\"pname\" : \"?\"}}}}")
    //@Query("{\"bool\" : {\"must\" : {\"field\" : {\"pname\" : \"?\"}}}}")
    Iterator<DocGeoBean> findByPname(String pname);

}
