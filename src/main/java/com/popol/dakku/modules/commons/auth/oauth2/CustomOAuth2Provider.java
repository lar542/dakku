package com.popol.dakku.modules.commons.auth.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

public enum CustomOAuth2Provider {

	NAVER {
		
		@Override
		public ClientRegistration.Builder getBuilder(String registrationId) {
			ClientRegistration.Builder builder = getBuilder(registrationId, 
					ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL)
						.scope("email")
                        .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                        .tokenUri("https://nid.naver.com/oauth2.0/token")
                        .userInfoUri("https://openapi.naver.com/v1/nid/me")
                        .userNameAttributeName("response")
                        .clientName("Naver");
        	return builder;
		}
	},
	
	GOOGLE {

		@Override
		public Builder getBuilder(String registrationId) {
			ClientRegistration.Builder builder = getBuilder(registrationId,
					ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL);
			builder.scope("email");
			builder.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth");
			builder.tokenUri("https://www.googleapis.com/oauth2/v4/token");
			builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
			builder.userNameAttributeName(IdTokenClaimNames.SUB);
			builder.clientName("Google");
			return builder;
		}
	};
	
	private static final String DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}";
	
	protected final ClientRegistration.Builder getBuilder(
            String registrationId, ClientAuthenticationMethod method, String redirectUri) {

		ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId)
	            .clientAuthenticationMethod(method)
	            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
	            .redirectUriTemplate(redirectUri);
    	return builder;
	}
	
	public abstract ClientRegistration.Builder getBuilder(String registrationId);
}
