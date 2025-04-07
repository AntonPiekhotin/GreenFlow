package org.greenflow.authservice.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.authservice.model.dto.LoginRequest;
import org.greenflow.authservice.model.dto.SignupRequest;
import org.greenflow.authservice.model.dto.UserCreationDto;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.model.entity.role.RoleType;
import org.greenflow.authservice.model.entity.role.Roles;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;

    private static final String CLIENT_SERVICE_URL = "http://client/api/v1";

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User registerUser(@Valid SignupRequest signUpRequest) {
        if (signUpRequest.getRole().equals("CLIENT")) {
            return registerClient(signUpRequest);
        } else if (signUpRequest.getRole().equals("WORKER")) {
            return registerWorker(signUpRequest);
        }

        // should not be reached
        throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(), "Invalid role: " + signUpRequest.getRole());
    }

    private User registerClient(SignupRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Roles.of(RoleType.CLIENT))
                .authProvider("email")
                .build();
        user = userRepository.save(user);
        log.debug("Client saved in auth-service: {}", user.getEmail());

        if (!saveToClientService(user)) {
            throw new GreenFlowException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to save client "
                    + user.getEmail() + " in client-service");
        }

        log.info("Client registered: {}", user.getEmail());
        return user;
    }

    private User registerWorker(SignupRequest signUpRequest) {
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Roles.of(RoleType.WORKER))
                .authProvider("email")
                .build();
        userRepository.save(user);
        log.info("Worker registered: {}", user.getEmail());
        return user;
    }

    public User login(@Valid LoginRequest request) throws BadCredentialsException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        return findByEmail(request.getEmail()).get();
    }

    private boolean saveToClientService(User user) {
        UserCreationDto userCreationDto = UserCreationDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
        try {
            Boolean response = restTemplate.postForObject(CLIENT_SERVICE_URL + "/client/save", userCreationDto,
                    Boolean.class);
            if (response != null && response) {
                log.debug("Client saved in client-service: {}", user.getEmail());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Error occurred while saving client in client-service: {}", user.getEmail(), e);
            return false;
        }
    }
}
