package com.popol.dakku.modules.web;

import java.security.Principal;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.popol.dakku.modules.web.post.PostMapper;

@Controller
public class HomeController {
	
	@Resource(name = "postMapper")
	private PostMapper postMapper;
	
	@RequestMapping({"/", "/index"})
	public String index(Model model, Principal principal) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		
		return "index";
	}
	
	/**
	 * 레프트 메뉴 주소
	 * @param menuCode
	 * @param model
	 * @return
	 */
	@RequestMapping({
		"/post/list/{menuCode}",
		"/auth/inven/{menuCode}",
		"/shop/{menuCode}",
		"/rank/{menuCode}"
	})
	public String index(@PathVariable String menuCode, Model model) {
		Map pageinfo =  postMapper.getPageNm(menuCode);
		if(pageinfo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page Not Found");
		}
		model.addAttribute("pageinfo", pageinfo);
		
		if(StringUtils.startsWith(menuCode, "M01") || StringUtils.startsWith(menuCode, "M02")) { //게시판
			return "forward:/posts/" + menuCode;
		} else if (StringUtils.startsWith(menuCode, "M03")) { //소지품
			if(menuCode.equals("M03B001")) { //나의 아이템
				
			} else if(menuCode.equals("M03B002")) { //게시글 관리
				
			} else if(menuCode.equals("M03B003")) { //퀘스트
				return "forward:/quests";
			}
		} else if (menuCode.equals("M05")) { //상점
			
		} else if (menuCode.equals("M06")) { //랭킹
			
		}
		return null;
	}
}
