package com.example.mybatplusdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatplusdemo.entity.Menu;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.vo.MenuVO;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {
    List<MenuVO> allMenu();
}
