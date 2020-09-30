package com.zhiy.zhiyes7service.service;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-27 20:32
 */

import com.zhiy.zhiyes7service.bean.DocGeoBean;
import com.zhiy.zhiyes7service.rep.GeoElasticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service("geoElasticService")
public class GeoElasticServiceImpl implements IGeoElasticService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private GeoElasticRepository geoElasticRepository;

    private Pageable pageable = PageRequest.of(0,10);

    @Override
    public void createIndex() {
        elasticsearchTemplate.createIndex(DocGeoBean.class);
    }

    @Override
    public void deleteIndex(String index) {
        elasticsearchTemplate.deleteIndex(index);
    }

    @Override
    public void save(DocGeoBean docBean) {
        geoElasticRepository.save(docBean);
    }

    @Override
    public void saveAll(List<DocGeoBean> list) {
        geoElasticRepository.saveAll(list);
    }

    @Override
    public Iterator<DocGeoBean> findAll() {
        return geoElasticRepository.findAll().iterator();
    }

    @Override
    public Iterator<DocGeoBean> findByPname(String content) {
        return geoElasticRepository.findByPname(content);
    }

    @Override
    public Page<DocGeoBean> findGeoTest(String content) {
        return geoElasticRepository.findGeoTest(content,pageable);
    }


}

