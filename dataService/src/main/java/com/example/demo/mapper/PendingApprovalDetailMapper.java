package com.example.demo.mapper;

import com.example.demo.entity.PendingApproval;
import com.example.demo.entity.PendingApprovalDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PendingApprovalDetailMapper {

    List<PendingApprovalDetail> listByUserCode(Map<String, String> userCode);
}
