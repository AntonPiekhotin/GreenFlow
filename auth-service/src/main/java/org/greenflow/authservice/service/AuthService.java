package org.greenflow.authservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenflow.authservice.model.dto.LoginRequest;
import org.greenflow.authservice.model.dto.SignupRequest;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.model.entity.role.RoleType;
import org.greenflow.authservice.model.entity.role.Roles;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.greenflow.common.model.dto.UserCreationDto;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.beans.factory.annotation.Value;
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
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;

    @Value("${api.host.client}")
    private String CLIENT_HOST;

    @Value("${api.host.worker}")
    private String WORKER_HOST;

    @Value("${api.host.billing}")
    private String BILLING_HOST;

    private String CLIENT_SERVICE_URL;
    private String WORKER_SERVICE_URL;
    private String BILLING_SERVICE_URL;

    @PostConstruct
    public void init() {
        CLIENT_SERVICE_URL = "http://" + CLIENT_HOST + "/api/v1";
        WORKER_SERVICE_URL = "http://" + WORKER_HOST + "/api/v1";
        BILLING_SERVICE_URL = "http://" + BILLING_HOST + "/api/v1";
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        log.info("Checking if email exists: {}", email);
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User registerUser(@Valid SignupRequest signUpRequest) {
        try {
            log.info("Registering user: {}", signUpRequest);
            User user = new User();
            if (signUpRequest.getRole().equals("CLIENT")) {
                user = registerClient(signUpRequest);
            } else if (signUpRequest.getRole().equals("WORKER")) {
                user = registerWorker(signUpRequest);
            } else {
                throw new GreenFlowException(HttpStatus.BAD_REQUEST.value(),
                        "Invalid role: " + signUpRequest.getRole());
            }
            saveToBillingService(user);
            return user;
        } catch (Exception e) {
            log.error("Error occurred while registering user: {}", signUpRequest, e);
            throw new GreenFlowException(500, "Error occurred while registering user: " + e.getMessage());
        }
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
        log.debug("Worker saved in auth-service: {}", user.getEmail());

        if (!saveToWorkerService(user)) {
            throw new GreenFlowException(HttpStatus.SERVICE_UNAVAILABLE.value(), "Failed to save worker "
                    + user.getEmail() + " in worker-service");
        }
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

    private boolean saveToWorkerService(User user) {
        UserCreationDto userCreationDto = UserCreationDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
        try {
            Boolean response = restTemplate.postForObject(WORKER_SERVICE_URL + "/worker/save", userCreationDto,
                    Boolean.class);
            if (response != null && response) {
                log.debug("Worker saved in worker-service: {}", user.getEmail());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("Error occurred while saving worker in worker-service: {}", user.getEmail(), e);
            return false;
        }
    }

    private void saveToBillingService(User user) throws Exception {
        Boolean response = restTemplate.getForObject(BILLING_SERVICE_URL +
                "/billing/register?userId=" + user.getId(), Boolean.class);
        if (response != null && response) {
            log.debug("User saved in billing-service: {}", user.getEmail());
        } else {
            log.error("Failed to save user in billing-service: {}", user.getEmail());
        }
    }

    public String getClientEmailFromAuthService(String userId) {
        log.debug("Fetching email for user ID: {}", userId);
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElse(null);
    }
}
