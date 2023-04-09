package com.example.mybatplusdemo.wrapper;

import com.example.mybatplusdemo.entity.BeanUtil;
import com.example.mybatplusdemo.entity.Menu;
import com.example.mybatplusdemo.tool.node.ForestNodeMerger;
import com.example.mybatplusdemo.vo.MenuVO;

import java.util.List;
import java.util.stream.Collectors;

public class MenuWrapper {

    public List<MenuVO> listNodeVO(List<MenuVO> list) {
//        List<MenuVO> collect = list.stream().map(menu -> BeanUtil.copy(menu, MenuVO.class)).collect(Collectors.toList());
        return ForestNodeMerger.merge(list);
    }
}
