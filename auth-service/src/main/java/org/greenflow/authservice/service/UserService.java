package org.greenflow.authservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.authservice.model.dto.LoginRequest;
import org.greenflow.authservice.model.dto.SignupRequest;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.model.entity.role.RoleType;
import org.greenflow.authservice.model.entity.role.Roles;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(SignupRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Roles.of(RoleType.CLIENT))
                .authProvider("email")
                .build();

        log.info("User registered: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User login(@Valid LoginRequest request) throws BadCredentialsException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return findByEmail(request.getEmail()).get();

    }
}
