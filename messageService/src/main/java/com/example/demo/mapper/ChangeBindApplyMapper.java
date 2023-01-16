package com.example.demo.mapper;

import com.example.demo.entity.BindApply;
import com.example.demo.entity.ChangeBindApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChangeBindApplyMapper {

    public int add(ChangeBindApply changeBndApply);

    public int review(int id);

    public List<ChangeBindApply> list(@Param(value="deleted") int deleted);

    public ChangeBindApply get(int id);

    public ChangeBindApply getUnReviewByServerIdAndOpenIdAndUserCode(ChangeBindApply changeBndApply);

    int removeById(int id);
}