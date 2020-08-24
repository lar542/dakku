package com.popol.dakku.modules.commons.interceptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.popol.dakku.modules.web.menu.MenuMapper;

public class MenuLoadInterceptor extends HandlerInterceptorAdapter {
	
	private MenuMapper menuMapper;
	
	public MenuLoadInterceptor(MenuMapper menuMapper) {
		this.menuMapper = menuMapper;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		List<Map> menuList = menuMapper.getMenuList();
		Map resultList = new LinkedHashMap(); 
		
		for (int i = 0; i < menuList.size(); i++) {
			Map obj = menuList.get(i);
			if(obj.get("menu_parent").equals("ROOT")) {
				obj.put("subs", new ArrayList());
				resultList.put(obj.get("menu_code"), obj);
			} else {
				Map parentMap = (Map) resultList.get(obj.get("menu_parent"));
				List subs = (List) parentMap.get("subs");
				subs.add(obj);
			}
		}
		modelAndView.addObject("menuList", resultList);
	}
	
}
