package com.example.demo.mapper;

import com.example.demo.entity.BindApply;
import com.example.demo.entity.ChangeBindApply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChangeBindApplyMapper {

    public int add(ChangeBindApply changeBndApply);

    public int review(int id);

    public List<ChangeBindApply> list();

    public ChangeBindApply get(int id);

    public ChangeBindApply getUnReviewByServerIdAndOpenIdAndUserCode(ChangeBindApply changeBndApply);
}