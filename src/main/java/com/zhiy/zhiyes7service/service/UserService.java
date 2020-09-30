package com.zhiy.zhiyes7service.service;

import com.zhiy.zhiyes7service.bean.UserBean;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 20:59
 */

public interface UserService  {
    void saveAll(List<UserBean> list);

    Iterator<UserBean> findAll();

    void create();

    public AggregatedPage testForHigh() throws IOException;

}
