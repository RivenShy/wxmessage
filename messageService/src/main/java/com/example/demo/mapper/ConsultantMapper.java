package com.example.demo.mapper;


import com.example.demo.entity.Consultant;
import com.example.demo.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConsultantMapper {

    public Consultant get(int id);

    public List<Consultant> list(@Param(value="deleted") int deleted);

    int add(Consultant consultant);

    int update(Consultant consultant);

    int delete(int id);

    Consultant selectByConsultCode(String consultCode);

    int checkIfExistConsultCode(Consultant consultant);
}
