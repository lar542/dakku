package com.popol.dakku.modules.web.auth;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Update;

import com.popol.dakku.modules.commons.auth.vo.UserVO;

public interface AuthMapper {
	
	/**
	 * 사용자 조회
	 */
	public UserVO getUserInfo(String email);
	
	/**
	 * 사용자 추가
	 */
	public void addUser(UserVO userVO);
	
	/**
	 * 사용자 권한 추가
	 */
	public void addRole(UserVO userVO);
	
	/**
	 * 사용자 권한 조회 
	 */
	public List getRoles(Long u_id);
	
	/**
	 * 사용자 인벤에 추가
	 * 1. 작성 글
	 * 2. 아이템 추가 
	 */
	public void addItem(Map item);
	
	/**
	 * 사용자 닉네임 변경
	 */
	@Update("update user set nick = #{nick}, nick_chg_yn = 'N' where u_id = #{u_id}")
	public void modifyNick(UserVO userVO);
	
	/**
	 * 일반, 소셜 공통 : 회원 탈퇴
	 */
	@Update("update user set deleted_yn = 'Y' where u_id = #{u_id}")
	public void removeUser(int u_id);
	
	/**
	 * 오늘 첫 로그인 여부 확인
	 */
	public int getTodayLoginLog(Long u_id);
	
	/**
	 * 유저의 경험치 추가
	 */
	public void plusExp(Map map);
	
	/**
	 * 유저 레벨업
	 */
	public void levelUp(Map map);
}
