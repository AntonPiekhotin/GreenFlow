package org.greenflow.authservice.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.authservice.model.dto.LoginRequest;
import org.greenflow.authservice.model.dto.LoginResponse;
import org.greenflow.authservice.model.dto.SignupRequest;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.service.AuthService;
import org.greenflow.authservice.service.security.JwtService;
import org.greenflow.common.model.dto.ResponseErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;

    private final AuthService authService;

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Auth service is up and running!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest);
        String jwtToken = jwtService.generateToken(user);
        LoginResponse response = LoginResponse.builder()
                .username(user.getEmail())
                .jwtToken(jwtToken)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signUpRequest) {
        log.debug("Registering user: {}", signUpRequest);
        if (authService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(ResponseErrorDto.builder()
                    .statusCode(400)
                    .errorMessage(List.of("Email is already in use!"))
                    .build());
        }
        User user = authService.registerUser(signUpRequest);
        String jwtToken = jwtService.generateToken(user);
        LoginResponse response = LoginResponse.builder()
                .username(user.getEmail())
                .jwtToken(jwtToken)
                .build();
        return ResponseEntity.status(201).body(response);
    }

}
