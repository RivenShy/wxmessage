package com.example.demo.service;


import com.example.demo.entity.AuditDelayCount;

import java.util.List;

public interface IAuditDelayCountService {

    public List<AuditDelayCount> list();

    AuditDelayCount getByUserCode(String userCode);
}
