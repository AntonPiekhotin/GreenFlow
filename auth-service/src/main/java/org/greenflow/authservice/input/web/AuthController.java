package org.greenflow.authservice.input.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenflow.authservice.model.dto.LoginRequest;
import org.greenflow.authservice.model.dto.LoginResponse;
import org.greenflow.authservice.model.dto.SignupRequest;
import org.greenflow.authservice.model.entity.Role;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.greenflow.authservice.service.security.JwtUtils;
import org.greenflow.authservice.service.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//        String token = jwtUtils.generateToken(authentication);
//        return ResponseEntity.ok(Collections.singletonMap("token", token));

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

//      Set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = LoginResponse.builder()
                .username(userDetails.getUsername())
                .jwtToken(jwtToken)
                .roles(roles)
                .build();

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .userName(signUpRequest.getUsername())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();


//        if (strRoles == null || strRoles.isEmpty()) {
//            role = roleRepository.findByRoleName(Role.RoleType.CLIENT)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//        } else {
//            String roleStr = strRoles.iterator().next();
//            if (roleStr.equals("admin")) {
//                role = roleRepository.findByRoleName(Role.RoleType.ADMIN)
//                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            } else {
//                role = roleRepository.findByRoleName(Role.RoleType.CLIENT)
//                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            }
//        }
        user.setAuthProvider("email");
        user.setRoles(Role.of(Role.RoleType.CLIENT));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }


}
