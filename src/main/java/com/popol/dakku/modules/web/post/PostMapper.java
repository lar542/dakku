package com.popol.dakku.modules.web.post;

import java.util.List;
import java.util.Map;

import com.popol.dakku.modules.commons.vo.PaginationInfoVO;

public interface PostMapper {
	
	/**
	 * 현재 페이지 이름
	 */
	public Map getPageNm(String menuCode);
	
	/**
	 * 총 게시글 수 조회
	 */
	public int getTotalRecord(PaginationInfoVO paginationInfoVO);
	
	/**
	 * 게시글 목록 조회
	 */
	public List getPostList(PaginationInfoVO paginationInfoVO);
	
	/**
	 * 게시글 조회
	 */
	public Map getPost(Long p_id);
	
	/**
	 * 게시글의 좋아요/싫어요 조회
	 */
	public List getEmotionCntOfPost(Long p_id);
	
	/**
	 * 게시글의 댓글 조회 
	 */
	public List getComments(Long p_id);
	
	/**
	 * 게시글 종류 조회
	 */
	public List getPostTitle();
	
	/**
	 * 게시글 상태 조회
	 */
	public List getPostState(String menu_code);
	
	/**
	 * 게시글 파일 추가
	 */
	public void saveFile(Map file);
	
	/**
	 * 게시글 파일 상태 변경
	 * submit_yn
	 * Y -> 게시글 submit 완료
	 * N -> 게시글 submit은 하지  않음
	 * X -> 게시글 작성 중 이미지 삭제
	 */
	public void modifyFile(Map map);
	
	/**
	 * 게시글 추가
	 */
	public void savePost(Map map);
	
	/**
	 * 해당 게시글의 파일 목록 
	 */
	public List getFiles(Long p_id);
	
	/**
	 * 게시글 수정
	 */
	public void modifyPost(Map map);
	
	/**
	 * 게시글 삭제
	 */
	public void removePost(Map map);
	
	/**
	 * 파일 삭제
	 */
	public void removeFile(Long p_id);
	
	/**
	 * 조회수 증가
	 */
	public void plusViewCnt(Long p_id);
	
	/**
	 * 게시글 평가 여부
	 * 한 게시글 당 표현 1개만 가능
	 */
	public String getPostEmotion(Map map);
	
	/**
	 * 게시글 평가
	 */
	public void addPostEmotion(Map map);
	public void plusEmotion(Map map);
	
	/**
	 * 게시글의 좋아요/싫어요 취소 
	 */
	public void removeEmotion(Map map);
	public void minusEmotion(Map map);
	
	/**
	 * 댓글 추가
	 */
	public void saveComment(Map map);
	
}
