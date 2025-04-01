package org.greenflow.authservice.config;

import lombok.RequiredArgsConstructor;
import org.greenflow.authservice.model.entity.role.Role;
import org.greenflow.authservice.model.entity.role.Roles;
import org.greenflow.authservice.model.entity.role.RoleType;
import org.greenflow.authservice.model.entity.User;
import org.greenflow.authservice.output.persistent.RoleRepository;
import org.greenflow.authservice.output.persistent.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        rolesInit();
        adminInit();
    }

    private void rolesInit() {
        Set<Role> roles = Roles.of(RoleType.ADMIN,
                RoleType.MANAGER,
                RoleType.CLIENT);
        roleRepository.saveAll(roles);
    }

    private void adminInit() {
        if (userRepository.existsByEmail("admin@admin.com")) {
            return;
        }
        User admin = User.builder()
                .email("admin@admin.com")
                .password(passwordEncoder.encode("admin"))
                .roles(Roles.of(RoleType.ADMIN, RoleType.MANAGER))
                .build();
        userRepository.save(admin);
        if (userRepository.existsByEmail("manager@manager.com")) {
            return;
        }
        User manager = User.builder()
                .email("manager@manager.com")
                .password(passwordEncoder.encode("manager"))
                .roles(Roles.of(RoleType.MANAGER))
                .build();
        userRepository.save(manager);
    }
}