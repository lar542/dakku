package com.popol.dakku.modules.commons.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.popol.dakku.modules.commons.auth.vo.UserVO;

public class UserDetailsHelper {

//	@Resource(name = "securityOAuthUserDetailsService")
//	static IUserDetailsService userDetailsService;
//	
//	public IUserDetailsService getUserDetailsService() {
//		return userDetailsService;
//	}
	
	/**
	 * @return 인증된 사용자 객체
	 */
	public static Object getAuthenticatedUser() {
		if(!isAuthenticated()) {
			return null;
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2User auth2User = (OAuth2User) authentication.getPrincipal();
		Map attrs = auth2User.getAttributes();
		UserVO userVO = UserVO.builder()
						.u_id((Long) attrs.get("uId"))
						.email(attrs.get("email").toString())
						.build();
		return userVO;
	}
	
	/**
	 * @return 인증된 사용자의 권한 정보
	 */
	public static List<String> getAuthorities() {
		List<String> result = new ArrayList<String>();
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		authorities.forEach(auth -> {
			result.add(auth.getAuthority());
		});
		return result;
	}
	
	/**
	 * @return 사용자의 인증 여부
	 * @throws ClassNotFoundException 
	 */
	public static Boolean isAuthenticated() {
		Object pincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(pincipal.getClass().getTypeName().equals("java.lang.String")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
