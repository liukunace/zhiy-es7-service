package com.zhiy.zhiyes7service.controller;

import com.zhiy.zhiyes7service.bean.DocBean;
import com.zhiy.zhiyes7service.bean.DocGeoBean;
import com.zhiy.zhiyes7service.bean.UserBean;
import com.zhiy.zhiyes7service.service.IElasticService;
import com.zhiy.zhiyes7service.service.IGeoElasticService;
import com.zhiy.zhiyes7service.service.UserServiceImpl;
import com.zhiy.zhiyes7service.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-27 20:25
 */
@RestController
@RequestMapping("/es")
public class EsController {

    @GetMapping("test")
    public R test(){
        return R.ok().put("status","success");
    }

    @Autowired
    private IElasticService elasticService;

    @Autowired
    private IGeoElasticService geoElasticService;

    @GetMapping("/init")
    public void init() {
        geoElasticService.createIndex();
    }

    @GetMapping("/add")
    public R add(){
        //[116.769934726936,36.3857351256274],[116.769721237499,36.385702663353],[116.769686704257,36.3857674465787]
        List<Float> a1=new ArrayList<Float>();
        a1.add((float)116.769934);
        a1.add((float)36.38573512562);

        List<Float> a2=new ArrayList<Float>();
        a2.add((float)116.769934);
        a2.add((float)36.38573512562);

        List<List<Float>> aa1=new ArrayList<>();
        aa1.add(a1);
        aa1.add(a2);
        List<List<List<Float>>> aa2=new ArrayList<>();
        aa2.add(aa1);

        List<DocBean> list =new ArrayList<>();
        list.add(new DocBean(5L,"XX0193","XX8064","测试1",1,aa2));
        list.add(new DocBean(6L,"XX0210","XX7475","正式1",1,aa2));
        //list.add(new DocBean(3L,"XX0257","XX8097","xxxxxxxxxxxxxxxxxx",1));
        elasticService.saveAll(list);
        return R.ok().put("status","add success");
    }

    @GetMapping("/all")
    public Iterator<DocBean> all(){
        return elasticService.findAll();
    }

    @GetMapping("/f1")
    public Page<DocBean> f1(){
        return elasticService.findByContent("测试");
    }

    @GetMapping("/gall")
    public Iterator<DocGeoBean> geoall(){
        return geoElasticService.findAll();
    }

    @GetMapping("/search")
    public R geosearch(@RequestParam("name") String name){
        Iterator<DocGeoBean> list=geoElasticService.findByPname(name);
        return  R.ok().put("list",list);
    }


}
