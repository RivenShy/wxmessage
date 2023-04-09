package com.example.mybatplusdemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.mybatplusdemo.service.IMenuService;
import com.example.mybatplusdemo.utils.R;
import com.example.mybatplusdemo.vo.MenuVO;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;



/**
 * 控制器
 *
 * @author Chill
 */
//@NonDS
@RestController
@AllArgsConstructor
@RequestMapping("/menu")
@Api(value = "菜单", tags = "菜单")
//public class MenuController extends RabbitController {
public class MenuController  {

	private final IMenuService menuService;

	/**
	 * 前端菜单数据
	 */
	@GetMapping("/routes")
//	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "前端菜单数据", notes = "前端菜单数据")
	public R<List<MenuVO>> routes(Long topMenuId) {
//		String language = I18nUtils.getCurrentLang();
		List<MenuVO> list = menuService.routes(null, topMenuId, null);
		return R.data(list);
	}
}
