package com.example.demo.service;

import com.example.demo.entity.ChangeBindApply;

import java.util.List;


public interface ChangeBindApplyService {
    public int add(ChangeBindApply changeBindApply);

    public int review(int id);

    public List<ChangeBindApply> list();

    public ChangeBindApply get(int id);

    public ChangeBindApply getUnReviewByServerIdAndOpenIdAndUserCode(ChangeBindApply changeBndApply);
}
