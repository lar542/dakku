package com.popol.dakku.modules.commons.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.popol.dakku.modules.commons.auth.oauth2.OAuthAttributes;
import com.popol.dakku.modules.commons.auth.vo.UserVO;
import com.popol.dakku.modules.web.actlog.ActLogMapper;
import com.popol.dakku.modules.web.auth.AuthMapper;

/**
 * 소셜 로그인 후 가져온 사용자 정보를 기반으로 
 * 가입, 정보 수정, 세션 저장 등의 기능 지원
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Autowired
	private AuthMapper authMapper;
	@Autowired
	private ActLogMapper actLogMapper;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);
		
		//현재 로그인 진행 중인 서비스 구분 코드
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		//OAuth2 로그인 진행 시 키가 되는 필드 값, 구글의 경우 기본적으로 코드를 지원함(구글의 기본 코드는 sub)
		//네이버는 유저 정보를 하위 필드인 response에 담아 주기 때문에 response로 가져옴
		String userNameAttributeName = userRequest.getClientRegistration()
					.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
		
		//OAuth2User의 attribute를 담을 클래스
		OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
		
		//사용자 등록 및 조회, 로그인 기록 추가
		UserVO userVO = addUserOrAddUserLog(attributes.getEmail(), attributes.getAuthId(), attributes.getRegistrationId());
		attributes.putUserVO(userVO);
		
		List<Map> roles = userVO.getRoles();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (Map role : roles) {
			GrantedAuthority grantAuth = new SimpleGrantedAuthority((String) role.get("role_code"));
			authorities.add(grantAuth);
		}
		return new DefaultOAuth2User(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());
	}

	/**
	 * 사용자 등록 및 조회, 로그인 기록 추가 
	 */
	@Transactional
	public UserVO addUserOrAddUserLog(String email, String id, String snsType) {
		UserVO userVO = authMapper.getUserInfo(email);
		if(userVO == null) {
			userVO = UserVO.builder()
						.email(email)
						.nick("새싹다꾸" + id)
						.sns_type(snsType)
						.sns_id(id)
						.lv_code("LV1")
						.roles(Arrays.asList("ROLE_USER"))
						.build();
			authMapper.addUser(userVO); //유저 추가
			authMapper.addRole(userVO); //권한 추가
			//다시 조회
			userVO = authMapper.getUserInfo(email);
		}
		//권한 조회
		userVO.setRoles(authMapper.getRoles(userVO.getU_id()));
		//오늘 첫 로그인 경험치
		int accCnt = authMapper.getTodayLoginLog(userVO.getU_id());
		if(accCnt == 0) {
			Map map = new HashMap();
			map.put("u_id", userVO.getU_id());
			map.put("plusExp", userVO.getAdditional_exp());
			authMapper.plusExp(map); //경험지 추가
			userVO.setExp(userVO.getExp() + userVO.getAdditional_exp());
			//레벨업
			if(userVO.getExp() >= userVO.getNext_required_exp()) {
				String lvCode = "LV" + userVO.getLv() + 1;
				userVO.setLv_code(lvCode);
				userVO.setLv(userVO.getLv() + 1);
				map.put("lv_code", lvCode);
				authMapper.levelUp(map);
				map.put("al_type", "U");
				map.put("al_content", "[레벨업] " + lvCode);
				actLogMapper.addActLog(map);
			}
		}
		//로그인 기록 추가
		Map<String, Object> log = new HashMap<String, Object>();
		log.put("al_type", "L");
		log.put("al_content", userVO.getSns_type() + " 로그인");
		log.put("u_id", userVO.getU_id());
		actLogMapper.addActLog(log);
		return userVO;
	}
}
