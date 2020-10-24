package com.popol.dakku.modules.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.popol.dakku.modules.commons.util.FileUtil;
import com.popol.dakku.modules.web.post.PostMapper;

@Controller
public class HomeController {
	
	@Resource(name = "postMapper")
	private PostMapper postMapper;
	
	@RequestMapping({"/", "/index"})
	public String index(Model model, HttpServletRequest req) throws IOException {
		//포인트 스티커 표지
		String pointStickerPath = "/resources/img/sticker/point/";
		List stickerList = new ArrayList();
		Map<String, Object> stickerDirMap = FileUtil.getPathFiles(req.getServletContext(), pointStickerPath);
		if(stickerDirMap.get("resultCode").toString().equals("success")) {
			
			List<File> stickerDirs = (List<File>) stickerDirMap.get("files");
			List slRow = new ArrayList();
			for (int i = 0; i < stickerDirs.size(); i++) {
				Map item = new HashMap();
				item.put("name", stickerDirs.get(i).getName());
				Map<String, Object> stickerMap = FileUtil.getPathFiles(req.getServletContext(), pointStickerPath + stickerDirs.get(i).getName());
				List<File> stickers = (List<File>) stickerMap.get("files");
				String stickerImg = pointStickerPath + stickerDirs.get(i).getName() + "/" + stickers.get(0).getName();
				item.put("img", stickerImg);
				slRow.add(item);
				if(i % 2 != 0) {
					stickerList.add(slRow);
					slRow = new ArrayList();
				}
			}
		}
		model.addAttribute("stickerList", stickerList);
		return "common/dakku";
	}
	
	@GetMapping(value = "/stickers", produces = "application/json;charset=utf8")
	@ResponseBody
	public List stickers(@RequestParam String type, @RequestParam String name, HttpServletRequest req) throws IOException {
		String stickerPath = "/resources/img/sticker/" + type + "/" + name;
		Map<String, Object> stickerDirMap = FileUtil.getPathFiles(req.getServletContext(), stickerPath);
		List list = new ArrayList();
		if(stickerDirMap.get("resultCode").toString().equals("success")) {
			List<File> files = (List<File>) stickerDirMap.get("files");
			for (int i = 0; i < files.size(); i++) {
				list.add(files.get(i).getName());
			}
		}
		return list;
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
