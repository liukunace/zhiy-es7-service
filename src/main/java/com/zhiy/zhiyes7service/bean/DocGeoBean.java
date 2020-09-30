package com.zhiy.zhiyes7service.bean;

/**
 * @description:
 * @author: liukun
 * @create: 2020-09-27 20:30
 */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "example092805",type = "_doc", shards = 1, replicas = 0)
public class DocGeoBean {

    @Id
    private String id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String pname;

    @Field(type = FieldType.Text)
    private String location;

    public DocGeoBean(String id, String pname, String location) {
        this.id = id;
        this.pname = pname;
        this.location = location;
    }
}

