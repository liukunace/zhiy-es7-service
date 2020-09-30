package com.zhiy.zhiyes7service.service;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-27 20:32
 */

import com.zhiy.zhiyes7service.bean.DocGeoBean;
import org.springframework.data.domain.Page;

import java.util.Iterator;
import java.util.List;

public interface IGeoElasticService {

    void createIndex();

    void deleteIndex(String index);

    void save(DocGeoBean docBean);

    void saveAll(List<DocGeoBean> list);

    Iterator<DocGeoBean> findAll();

    Iterator<DocGeoBean> findByPname(String content);

    Page<DocGeoBean> findGeoTest(String content);

}

