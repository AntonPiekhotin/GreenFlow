package org.greenflow.authservice.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.greenflow.authservice.model.dto.LoginResponse;
import org.greenflow.authservice.model.entity.Role;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        var uri = request.getRequestURI();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userService.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oAuth2User.getName());
            newUser.setRoles(Role.of(Role.RoleType.CLIENT));
            newUser.setAuthProvider("google");
            userService.registerUser(newUser);
            return newUser;
        });

        String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getRoles());
        LoginResponse loginResponse = LoginResponse.builder()
                .username(user.getEmail())
                .jwtToken(jwtToken)
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!response.isCommitted()) {
            OutputStream out = response.getOutputStream();
            new ObjectMapper().writeValue(out, loginResponse);
            out.flush();
        }

        this.setRedirectStrategy((req, res, url) -> {
        });

    }

}
