package com.popol.dakku.modules.web.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.popol.dakku.modules.commons.auth.UserDetailsHelper;
import com.popol.dakku.modules.commons.auth.vo.UserVO;
import com.popol.dakku.modules.commons.consts.VarConsts;
import com.popol.dakku.modules.commons.util.FileUtil;
import com.popol.dakku.modules.commons.vo.PaginationInfoVO;

@Controller
public class PostController {
	
	@Resource(name = "postMapper")
	private PostMapper postMapper;
	
	@GetMapping("/post/list/{menuCode}")
	public String list(@PathVariable String menuCode, HttpServletRequest req, Model model) {
		Map pageinfo =  postMapper.getPageNm(menuCode);
		if(pageinfo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page Not Found");
		}
		model.addAttribute("pageinfo", pageinfo);
		addAttr(menuCode, req, model);
		return "post/list";
	}
	
	@GetMapping("/post/{menuCode}/{postId}")
	public String view(@PathVariable String menuCode, @PathVariable Long postId, HttpServletRequest req, HttpServletResponse res, Model model) {
		Map pageinfo =  postMapper.getPageNm(menuCode);
		if(pageinfo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page Not Found");
		}
		model.addAttribute("pageinfo", pageinfo);
		//쿠키값이 없을 때만 조회수 증가
		viewCookie(req.getCookies(), postId, res);
		//게시글
		Map post = postMapper.getPost(postId);
		model.addAttribute("p", post);
		//게시글의 좋아요/싫어요 카운트
//		List<Map> emotionResults = postMapper.getEmotionCntOfPost(postId);
//		for (Map map : emotionResults) {
//			post.put(map.get("emotion"), map.get("cnt"));
//		}
		//로그인 상태일 때 이미 게시글에 좋아요/싫어요 했는지
		model.addAttribute("alreadyEmotion", "null");
		Object obj = UserDetailsHelper.getAuthenticatedUser();
		if(obj != null) {
			UserVO userVO = (UserVO) obj;
			Map params = new HashMap<String, Object>();
			params.put("u_id", userVO.getU_id());
			params.put("pId", postId);
			String alreadyEmotion = postMapper.getPostEmotion(params);
			model.addAttribute("alreadyEmotion", alreadyEmotion + "");
		}
		
		//댓글
		List<Map> comments = postMapper.getComments(postId);
		model.addAttribute("comments", comments);
		//게시글 목록
		addAttr(menuCode, req, model);
		return "post/view";
	}
	
	private void viewCookie(Cookie[] cookies, Long postId, HttpServletResponse response) {
		//저장된 쿠키 중 조회수 쿠키 찾기
		String viewCnt = "";
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				if(cookie.getName().equals("VIEWCNT")) {
					viewCnt = cookie.getValue();
					break;
				}
			}
		}
		String newViewCnt = "|" + postId;
		//저장된 쿠키에 조회할 게시글 번호가 있는 지 검사
		if(StringUtils.indexOfIgnoreCase(viewCnt, newViewCnt) == -1) {
			Cookie cookie = new Cookie("VIEWCNT", viewCnt + newViewCnt);
			response.addCookie(cookie);
			postMapper.plusViewCnt(postId);
		}
	}
	
	private void addAttr(String menuCode, HttpServletRequest req, Model model) {
		int currentPage = 1;
		String page = req.getParameter("page");

		if(StringUtils.isNumeric(page)) {
			currentPage = Integer.parseInt(page);
		}
		
		PaginationInfoVO paginationInfoVO = new PaginationInfoVO();
		paginationInfoVO.setMenuCode(menuCode);
		paginationInfoVO.setMsCode(req.getParameter("msCode"));
		paginationInfoVO.setSearchType(req.getParameter("searchType"));
		paginationInfoVO.setSearchWord(req.getParameter("searchWord"));
		int totalRecord = postMapper.getTotalRecord(paginationInfoVO);
		paginationInfoVO.setTotalRecord(totalRecord);
		paginationInfoVO.setCurrentPage(currentPage);
		List posts = postMapper.getPostList(paginationInfoVO);
		
		model.addAttribute("posts", posts);
		model.addAttribute("pageVO", paginationInfoVO);
	}
	
	@GetMapping("/auth/post/write")
	public String write(Model model) {
		model.addAttribute("types", postMapper.getPostTitle());
		model.addAttribute("states", postMapper.getPostState("M01B002"));
		return "post/write";
	}
	
	@GetMapping(value = "/state/{menuCode}")
	@ResponseBody
	public List postState(@PathVariable String menuCode) {
		Map pageinfo =  postMapper.getPageNm(menuCode);
		if(pageinfo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page Not Found");
		}
		return postMapper.getPostState(menuCode);
	}
	
	@RequestMapping(value = "/upload/img", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map uploadImage(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest req) throws Exception {
		Map params = new HashMap<String, Object>();
		String[] extensions = {".jpg",".jpeg",".png",".gif"};
		params.put("extensions", extensions);
		params.put("upload_path", VarConsts.SUMMERNOTE_UPLOAD_PATH);
		params.put("url", req.getContextPath() + VarConsts.SUMMERNOTE_URL);
		Map fileMap = FileUtil.uploadFile(multipartFile, params);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resultCode", fileMap.get("resultCode"));
		if(fileMap.get("resultCode").toString().equals("success")) {
			postMapper.saveFile(fileMap);
			result.put("stored_file_nm", fileMap.get("stored_file_nm"));
			result.put("url", fileMap.get("url"));
			result.put("f_id", fileMap.get("f_id"));
		}
		return result;
	}
	
	@PostMapping(value = "/delete/img", produces = "application/json")
	@ResponseBody
	public Map uploadImageDelete(@RequestParam Map param) {
		String storedFilePath = VarConsts.SUMMERNOTE_UPLOAD_PATH + param.get("stored_file_nm").toString();
		Map resultMap = FileUtil.deleteFile(storedFilePath);
		
		if(resultMap.get("resultCode").toString().equals("success")) {
			resultMap.put("submit_yn", "X");
			resultMap.put("f_id", param.get("f_id"));
			postMapper.modifyFile(resultMap);
		}
		return resultMap;
	}
	
	@PostMapping(value = "/auth/save/post")
	@ResponseBody
	public Map save(@RequestParam Map params) throws Exception {
		//게시글 저장
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		params.put("u_id", userVO.getU_id());
		postMapper.savePost(params);
		//파일 상태 정보 변경, 파일의 게시글 번호 업데이트
		if(ObjectUtils.isNotEmpty(params.get("files"))) {
			params.put("submit_yn", "Y");
			String[] files = params.get("files").toString().split(",");
			for (String file : files) {
				params.put("f_id", file);
				params.put("p_id", params.get("p_id"));
				postMapper.modifyFile(params);
			}
		}
		Map result = new HashMap<String, String>();
		result.put("redirect", "/post/"+params.get("postType")+"/"+params.get("p_id"));
		return result;
	}
	
	@PostMapping("/auth/post/get")
	public String writeModify(@RequestParam Long pId, Model model) {
		Map post = postMapper.getPost(pId);
		List<Map> files = postMapper.getFiles(pId);
		for (Map file : files) {
			file.put("url", VarConsts.SUMMERNOTE_URL + file.get("stored_file_nm"));
		}
		model.addAttribute("p", post);
		model.addAttribute("files", files);
		model.addAttribute("types", postMapper.getPostTitle());
		model.addAttribute("states", postMapper.getPostState(post.get("menu_code").toString()));
		return "post/modify";
	}
	
	@PostMapping("/auth/modify/post")
	@ResponseBody
	public Map modify(@RequestParam Map params) {
		//게시글 수정
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		params.put("u_id", userVO.getU_id());
		postMapper.modifyPost(params);
		//파일 상태 정보 변경, 파일의 게시글 번호 업데이트
		if(ObjectUtils.isNotEmpty(params.get("files"))) {
			params.put("submit_yn", "Y");
			String[] files = params.get("files").toString().split(",");
			for (String file : files) {
				params.put("f_id", file);
				params.put("p_id", params.get("pId"));
				postMapper.modifyFile(params);
			}
		}
		Map result = new HashMap<String, String>();
		result.put("redirect", "/post/"+params.get("postType")+"/"+params.get("pId"));
		return result;
	}
	
	@PostMapping("/auth/remove/post")
	public String remove(@RequestParam Map params) {
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		params.put("u_id", userVO.getU_id());
		//게시글 삭제
		postMapper.removePost(params);
		//파일 상태 정보 변경, 실제 파일 삭제
		List files = postMapper.getFiles(Long.parseLong(params.get("pId").toString()));
		if(files.size() > 0) {
			postMapper.removeFile(Long.parseLong(params.get("pId").toString()));
			FileUtil.deleteFiles(files);
		}
		return "redirect:/post/list/" + params.get("menuCode");
	}
	
	@PostMapping("/auth/emotion")
	@ResponseBody
	public Map emotion(@RequestParam Map params) {
		Map result = new HashMap<String, Object>();
		String emotionChk = params.get("emotion").toString();
		if(!(emotionChk.equals("G") || emotionChk.equals("B"))) {
			result.put("emotion", "N");
			return result;
		}
		UserVO userVO = (UserVO) UserDetailsHelper.getAuthenticatedUser();
		params.put("u_id", userVO.getU_id());
		String alreadyEmotion = postMapper.getPostEmotion(params);
		if(alreadyEmotion == null) {
			postMapper.plusEmotion(params);
			postMapper.addPostEmotion(params);
			result.put("emotion", "A");
		} else {
			if(emotionChk.equals(alreadyEmotion)) {
				postMapper.minusEmotion(params);
				postMapper.removeEmotion(params);
				result.put("emotion", "C");
			} else {
				result.put("emotion", alreadyEmotion);
			}
		}
		List<Map> emotionResults = postMapper.getEmotionCntOfPost(Long.parseLong(params.get("pId").toString()));
		result.put("G", 0);
		result.put("B", 0);
		for (Map map : emotionResults) {
			result.put(map.get("emotion"), map.get("cnt"));
		}
		return result;
	}
}
