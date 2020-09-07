package com.popol.dakku.modules.web.userpost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.popol.dakku.modules.commons.auth.UserDetailsHelper;
import com.popol.dakku.modules.commons.auth.vo.UserVO;
import com.popol.dakku.modules.commons.vo.PaginationInfoVO;
import com.popol.dakku.modules.web.post.PostMapper;

@Controller
public class UserPostController {

	@Resource(name = "postMapper")
	private PostMapper postMapper;
	
	@Resource(name = "userPostMapper")
	private UserPostMapper userPostMapper;
	
	@GetMapping("/auth/inven/post/list/{menuCode}")
	public String list(@PathVariable String menuCode, Model model) {
		Map pageinfo =  postMapper.getPageNm(menuCode);
		if(pageinfo.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page Not Found");
		}
		model.addAttribute("pageinfo", pageinfo);
		
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		List postsCnt = userPostMapper.getPostCntGroupByMenu(userVO.getU_id());
		List cmtsCnt = userPostMapper.getCmtCntGroupByMenu(userVO.getU_id());
		model.addAttribute("postsCnt", postsCnt);
		model.addAttribute("cmtsCnt", cmtsCnt);
		
		return "userpost/list";
	}
	
	@GetMapping("/auth/user/post/detils")
	@ResponseBody
	public Map details(@RequestParam Map params) {
		int currentPage = 1;
		if(params.containsKey("page")) {
			currentPage = Integer.parseInt(params.get("page").toString());
		}
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		PaginationInfoVO paginationInfoVO = new PaginationInfoVO(10, 10);
		paginationInfoVO.setUId(userVO.getU_id());
		paginationInfoVO.setMenuCode(params.get("menuCode").toString());
		int totalRecord = userPostMapper.getTotalCntOfPost(paginationInfoVO);
		paginationInfoVO.setTotalRecord(totalRecord);
		paginationInfoVO.setCurrentPage(currentPage);
		List posts = userPostMapper.getPostList(paginationInfoVO);
		Map result = new HashMap();
		result.put("posts", posts);
		result.put("pageVO", paginationInfoVO);
		return result;
	}
}
