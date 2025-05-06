package com.example.mybatplusdemo.ma.base;


import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface PetBaseService<T> extends IService<T> {
    boolean deleteLogic(List<Long> ids);

    boolean changeStatus(List<Long> ids, Integer status);
}
