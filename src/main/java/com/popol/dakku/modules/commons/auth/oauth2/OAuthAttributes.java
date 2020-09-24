package com.popol.dakku.modules.commons.auth.oauth2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.popol.dakku.modules.commons.auth.vo.UserVO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

	private String registrationId;
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String email;
	private String authId;
	
	@Builder
	public OAuthAttributes(String registrationId, Map<String, Object> attributes, String nameAttributeKey, String email, String authId) {
		this.registrationId = registrationId;
		this.attributes = attributes;
		this.nameAttributeKey= nameAttributeKey;
		this.email = email;
		this.authId = authId;
	}

	public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		if("naver".equals(registrationId)) {
			return ofNaver(registrationId, attributes);
		}
		return ofGoogle(registrationId, userNameAttributeName, attributes);
	}
	
	private static OAuthAttributes ofNaver(String registrationId, Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return OAuthAttributes.builder()
					.registrationId(registrationId)
					.email((String) response.get("email"))
					.attributes(response)
					.nameAttributeKey("id")
					.authId((String) response.get("id"))
					.build();
	}

	private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("sub", attributes.get("sub").toString());
		attr.put("email", attributes.get("email").toString());
		return OAuthAttributes.builder()
				   .registrationId(registrationId)
	               .email((String) attributes.get("email"))
	               .attributes(attr)
	               .nameAttributeKey(userNameAttributeName)
	               .authId(attributes.get("sub").toString())
	               .build();
	}
	
	public void putUserVO(UserVO userVO) {
		attributes.put("uId", userVO.getU_id());
		attributes.put("nick", userVO.getNick());
		attributes.put("exp", userVO.getExp());
		attributes.put("cheer", userVO.getCheer());
		attributes.put("stamp", userVO.getStamp());
		attributes.put("lv_code", userVO.getLv_code());
		attributes.put("icon_class", userVO.getIcon_class());
		attributes.put("icon_color", userVO.getIcon_color());
		attributes.put("lv", userVO.getLv());
		attributes.put("next_required_exp", userVO.getNext_required_exp());
	}
	
	public UserVO toEntity() {
		return UserVO.builder()
				.email(email)
				.roles(Arrays.asList("ROLE_USER"))
				.build();
	}
}
