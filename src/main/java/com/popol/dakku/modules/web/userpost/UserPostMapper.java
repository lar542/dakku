package com.popol.dakku.modules.web.userpost;

import java.util.List;

import com.popol.dakku.modules.commons.vo.PaginationInfoVO;

public interface UserPostMapper {

	/**
	 * 게시판별 작성 게시글 수
	 */
	public List getPostCntGroupByMenu(Long u_id);
	
	/**
	 * 게시판별 작성 댓글 수
	 */
	public List getCmtCntGroupByMenu(Long u_id);
	
	/**
	 * 해당 게시판에서 작성한 게시글 수 조회
	 */
	public int getTotalCntOfPost(PaginationInfoVO paginationInfoVO);
	
	/**
	 * 해당 게시판에서 작성한 게시글 조회
	 */
	public List getPostList(PaginationInfoVO paginationInfoVO);
}
