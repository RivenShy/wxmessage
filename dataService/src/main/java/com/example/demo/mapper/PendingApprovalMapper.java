package com.example.demo.mapper;

import com.example.demo.entity.PendingApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PendingApprovalMapper {

    public List<PendingApproval> list();

    public PendingApproval callProce(Map<String, String> map);

    public PendingApproval getByUserCode(String userCode);

    PendingApproval getAverageTime(Map<String, String> userCode);

    List<PendingApproval> getRank();
}
