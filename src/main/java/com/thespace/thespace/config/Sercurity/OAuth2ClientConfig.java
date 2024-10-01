//package com.thespace.thespace.config.Sercurity;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Map;
//
//@RequiredArgsConstructor
//public class OAuth2ClientConfig extends DefaultOAuth2UserService
//  {
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException
//      {
//        ClientRegistration clientRegistration = userRequest.getClientRegistration();
//        String registrationId = clientRegistration.getRegistrationId();
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = null;
//        switch(registrationId)
//
//        return generateDTO(email, attributes);
//      }
//  }
