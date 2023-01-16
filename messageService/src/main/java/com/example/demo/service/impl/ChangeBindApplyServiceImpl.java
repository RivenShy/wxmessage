package com.example.demo.service.impl;

import com.example.demo.entity.ChangeBindApply;
import com.example.demo.mapper.ChangeBindApplyMapper;
import com.example.demo.service.ChangeBindApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeBindApplyServiceImpl implements ChangeBindApplyService {

    @Autowired
    ChangeBindApplyMapper changeBindApplyMapper;

    @Override
    public int add(ChangeBindApply changeBindApply) {
        return changeBindApplyMapper.add(changeBindApply);
    }


    @Override
    public int review(int id) {
        return changeBindApplyMapper.review(id);
    }

    @Override
    public List<ChangeBindApply> list(int deleted) {
        return changeBindApplyMapper.list(deleted);
    }

    @Override
    public ChangeBindApply get(int id) {
        return changeBindApplyMapper.get(id);
    }

    @Override
    public ChangeBindApply getUnReviewByServerIdAndOpenIdAndUserCode(ChangeBindApply changeBndApply) {
        return changeBindApplyMapper.getUnReviewByServerIdAndOpenIdAndUserCode(changeBndApply);
    }

    @Override
    public int removeById(int id) {
        return changeBindApplyMapper.removeById(id);
    }

}
