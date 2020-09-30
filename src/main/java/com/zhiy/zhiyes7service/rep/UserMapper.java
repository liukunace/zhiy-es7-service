package com.zhiy.zhiyes7service.rep;

import com.zhiy.zhiyes7service.bean.UserBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 21:06
 */
@Service
public interface UserMapper extends ElasticsearchRepository<UserBean,Long> {
}
