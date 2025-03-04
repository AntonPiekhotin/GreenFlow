package org.greenflow.authservice.output.persistent;

import jakarta.annotation.PostConstruct;
import org.greenflow.authservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(Role.RoleType roleType);

    @PostConstruct
    default void initRoles() {
        for (Role.RoleType role : Role.RoleType.values()) {
            save(new Role(role));
        }
    }
}
