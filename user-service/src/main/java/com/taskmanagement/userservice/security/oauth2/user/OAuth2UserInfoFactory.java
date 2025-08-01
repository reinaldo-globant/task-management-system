package com.taskmanagement.userservice.security.oauth2.user;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("microsoft")) {
            return new MicrosoftOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}