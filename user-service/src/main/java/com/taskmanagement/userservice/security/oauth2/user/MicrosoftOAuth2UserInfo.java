package com.taskmanagement.userservice.security.oauth2.user;

import java.util.Map;

public class MicrosoftOAuth2UserInfo extends OAuth2UserInfo {

    public MicrosoftOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("displayName");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("mail");
    }

    @Override
    public String getImageUrl() {
        return null; // Microsoft Graph API requires additional call for profile photo
    }
}