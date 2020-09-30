package com.zhiy.zhiyes7service.controller;

import com.zhiy.zhiyes7service.bean.UserBean;
import com.zhiy.zhiyes7service.service.UserService;
import com.zhiy.zhiyes7service.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 21:16
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/init1")
    public String init1(){
        userService.create();
        // id name tags
        List<UserBean> list=new ArrayList<>();
        list.add(new UserBean(1,"张三锕爱","很帅,有很多人喜欢他"));
        list.add(new UserBean(2,"李四酷狗","很帅,但是讨厌他"));
        list.add(new UserBean(3,"王五王二爷","很丑,有是喜欢他"));
        list.add(new UserBean(4,"张三王二婆","很帅,有没人喜欢他"));
        userService.saveAll(list);
        return "success";
    }

}
