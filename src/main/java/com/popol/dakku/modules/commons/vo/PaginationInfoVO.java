package com.popol.dakku.modules.commons.vo;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("PagingVO")
@Data
public class PaginationInfoVO implements Serializable {

	private int screenSize = 50; 	//한 페이지의 리스트 수  
	private int blockSize = 10; 	//한 페이지의 페이지 링크 수  
	private int totalRecord; 		//총 게시글 수
	private int totalPage; 			//총 페이지 링크 수
	private int offset;
	private int startRow; 			//한 페이지의 시작 게시글 번호
	private int endRow; 			//한 페이지의 마지막 게시글 번호 
	private int startPage; 			//한 페이지의 시작 페이지 링크 번호
	private int endPage; 			//한 페이지의 마지막 페이지 링크 번호
	private int currentPage; 		//현재 페이지
	private int left;				//이전
	private int right;				//다음
	private String menuCode;		//게시판 종류
	private String msCode;			//현 게시판 게시글 상태
	private String searchType; 		//검색 조건
	private String searchWord; 		//검색어
	private Long uId;

	public PaginationInfoVO() {}
	
	public PaginationInfoVO(int screenSize, int blockSize) {
		this.screenSize = screenSize;
		this.blockSize = blockSize;
	}
	
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;									
		this.totalPage = (totalRecord + (screenSize-1)) / screenSize;	
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;									
		this.endRow = currentPage * screenSize;							
		this.startRow = endRow - (screenSize - 1);							
		this.offset = startRow - 1;
		this.endPage = (currentPage + (blockSize-1)) / blockSize * blockSize;	
		this.startPage = endPage - (blockSize-1);
		if(currentPage > blockSize) {
			this.left = startPage - blockSize;
		}
		if(totalPage > endPage) {
			this.right = endPage + 1;
		}
	}
	
	@Override
	public String toString() {
		return "[screenSize=" + screenSize + ", blockSize="
				+ blockSize + ", totalRecord=" + totalRecord + ", totalPage="
				+ totalPage + ", startRow=" + startRow + ", endRow=" + endRow
				+ ", startPage=" + startPage + ", endPage=" + endPage
				+ ", currentPage=" + currentPage + ", left=" + left + ", right=" + right + "]";
	}
}
