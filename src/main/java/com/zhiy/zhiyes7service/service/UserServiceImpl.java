package com.zhiy.zhiyes7service.service;

import com.zhiy.zhiyes7service.bean.UserBean;
import com.zhiy.zhiyes7service.rep.UserMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 21:02
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    UserMapper userMapper;
    @Override
    public Iterator<UserBean> findAll() {
        return userMapper.findAll().iterator();
    }
    public void create(){
        template.createIndex(UserBean.class);
    }

    @Override
    public void saveAll(List<UserBean> list) {
        userMapper.saveAll(list);
    }

    @Override
    public AggregatedPage testForHigh() throws IOException {
        String preTag = "<font color='#dd4b39'>";//google的色值
        String postTag = "</font>";
        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder()
                .must(new MatchQueryBuilder("tags","很帅"));
        //org.elasticsearch.index.query.QueryBuilder
        /*
        SearchQuery searchQuery=new NativeSearchQueryBuilder().   //总的查询
                withQuery(boolQueryBuilder).           //设置bool查询
                withHighlightFields(new HighlightBuilder.Field("name").preTags(preTag).postTags(postTag)).//设置高亮效果
                withHighlightFields(new HighlightBuilder.Field("tags").preTags(preTag).postTags(postTag)).build();
        AggregatedPage<UserBean> ideas=template.queryForPage(searchQuery, UserBean.class, new SearchResultMapper() {

            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<UserBean> list = new ArrayList<>();
                for(SearchHit  hit:searchResponse.getHits()){//获取遍历查询结果
                    if(searchResponse.getHits().getHits().length<=0)return null;
                    UserBean bean=new UserBean();
                    Map map=hit.getSourceAsMap();
                    System.out.println(map);
                    bean.setId((Integer)map.get("id"));
                    bean.setName((String)map.get("name"));
                    HighlightField name=hit.getHighlightFields().get("name");
                    if(name!=null){
                        bean.setName(name.fragments()[0].toString());   //得到高亮的结果
                    }
                    HighlightField tags=hit.getHighlightFields().get("tags");
                    if(tags!=null){
                        bean.setTags(tags.fragments()[0].toString());
                    }
                    list.add(bean);
                }
                if(list.size()>0)return new AggregatedPageImpl<>((List<T>)list);

                return null;
            }

//            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });


        ideas.get().forEach(model->{
            System.out.println(model);
        });
         */

        //return ideas;

        return null;
    }


}
