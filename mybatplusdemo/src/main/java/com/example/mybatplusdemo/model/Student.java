package com.example.mybatplusdemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document(indexName = "student",replicas = 0,shards = 3)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = -1479160581341748052L;

    /*ik_smart 粗精度，ik_max_word 细精度*/
    @Field(type = FieldType.Text,searchAnalyzer = "ik_smart",analyzer = "ik_max_word")
    private String name;

    /*text类型开启统计的时候需要设置属性fielddata = true 正排索引*/
    @Field(type = FieldType.Long)
    private Integer age;

    @Field(type = FieldType.Text)
    private String testNumber;

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type =  FieldType.Keyword)
    private String email;

    @Field(type =  FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape =JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date birthDay;

}
