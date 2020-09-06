package com.popol.dakku.modules.web.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
	
	private static String authorizationRequestBaseUri = "oauth2/authorization";
	Map<String, String> oauth2AuthenticationUrls = new HashMap<String, String>();
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	
	@RequestMapping(value = "/login")
	public String login(Model model) {
		Iterable<ClientRegistration> clientRegistrations = null;
	    ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
	    if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
	        clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
	    }
	    clientRegistrations.forEach(registration -> {
	    	oauth2AuthenticationUrls.put(registration.getClientName(), 
	    		      authorizationRequestBaseUri + "/" + registration.getRegistrationId());
	    }); 
	    model.addAttribute("urls", oauth2AuthenticationUrls);
		return "auth/login";
	}
}
