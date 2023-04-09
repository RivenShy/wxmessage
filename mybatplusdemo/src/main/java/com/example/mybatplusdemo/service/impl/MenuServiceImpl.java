package com.example.mybatplusdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mybatplusdemo.entity.Menu;
import com.example.mybatplusdemo.mapper.MenuMapper;
import com.example.mybatplusdemo.mapper.UserMapper;
import com.example.mybatplusdemo.model.User;
import com.example.mybatplusdemo.service.IMenuService;
import com.example.mybatplusdemo.vo.MenuVO;
import com.example.mybatplusdemo.wrapper.MenuWrapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    @Override
    public List<MenuVO> routes(String roleId, Long topMenuId, String language) {
        List<MenuVO> allMenus = baseMapper.allMenu();
        List<MenuVO> roleMenus = allMenus;

        return buildRoutes(allMenus, roleMenus, language);
    }

    private List<MenuVO> buildRoutes(List<MenuVO> allMenus, List<MenuVO> roleMenus, String language) {
//        language(roleMenus, null, language);

        List<MenuVO> routes = new LinkedList<>(roleMenus);
        // 角色拥有某个子菜单权限，则将子菜单的父菜单、爷爷菜单......放到list返回
        roleMenus.forEach(roleMenu -> recursion(allMenus, routes, roleMenu));
        routes.sort(Comparator.comparing(Menu::getSort));
        MenuWrapper menuWrapper = new MenuWrapper();
        //mapper里已过滤
//		List<Menu> collect = routes.stream().filter(x -> !Func.equals(x.getCategory(), 2)).collect(Collectors.toList());

        return menuWrapper.listNodeVO(routes);
    }

    private void recursion(List<MenuVO> allMenus, List<MenuVO> routes, Menu roleMenu) {
        Optional<MenuVO> menu = allMenus.stream().filter(x -> Funcequals(x.getId(), roleMenu.getParentId())).findFirst();
        if (menu.isPresent() && !routes.contains(menu.get())) {
            routes.add(menu.get());
            recursion(allMenus, routes, menu.get());
        }
    }

    public static boolean Funcequals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }
}
