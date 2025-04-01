package org.greenflow.authservice.output.persistent;

import jakarta.annotation.PostConstruct;
import org.greenflow.authservice.model.entity.role.Role;
import org.greenflow.authservice.model.entity.role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(RoleType roleType);

    @PostConstruct
    default void initRoles() {
        for (RoleType role : RoleType.values()) {
            save(new Role(role));
        }
    }
}
