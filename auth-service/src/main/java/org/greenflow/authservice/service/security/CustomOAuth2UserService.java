package org.greenflow.authservice.service.security;

import lombok.RequiredArgsConstructor;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getName();
//
//        Optional<User> existingUser = userRepository.findByEmail(email);
//        if (existingUser.isEmpty()) {
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setName(name);
//            newUser.setAuthProvider("GOOGLE");
//            userRepository.save(newUser);
//        }
//
//        return oAuth2User;
//    }
//}
