package org.greenflow.authservice.model.entity.role;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Roles {

    public static Set<Role> of(RoleType... roleTypes) {
        return Arrays.stream(roleTypes)
                .map(Role::new)
                .collect(Collectors.toSet());
    }

}
