package com.zhiy.zhiyes7service.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-28 20:57
 */

@Data
@NoArgsConstructor
@Document(indexName = "user_test")
public class UserBean {

    @Id
    private int id;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String name;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String tags;
    public UserBean(int id,String name,String tags){
        this.id=id;
        this.name=name;
        this.tags=tags;
    }

}
