package org.greenflow.authservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleType name;

    public Role(RoleType name) {
        this.name = name;
    }

    public Role() {
    }

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleType {
        ADMIN, MANAGER, CLIENT, WORKER
    }

    public static Set<Role> of(Role.RoleType... roleTypes) {
        return Arrays.stream(roleTypes)
                .map(Role::new)
                .collect(Collectors.toSet());
    }
}