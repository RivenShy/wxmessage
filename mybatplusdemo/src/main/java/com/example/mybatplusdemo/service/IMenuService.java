package com.example.mybatplusdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mybatplusdemo.entity.Menu;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.vo.MenuVO;

import java.util.List;

public interface IMenuService extends IService<Menu> {

    List<MenuVO> routes(String roleId, Long topMenuId, String language);
}
