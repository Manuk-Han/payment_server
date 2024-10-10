package com.study.payment.common;

import com.study.payment.entity.Role;
import com.study.payment.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        for (UserRoles role : UserRoles.values()) {
            if (!roleRepository.existsRoleByUserRoles(role)) {
                Role newRole = Role.builder().userRoles(role).build();
                roleRepository.save(newRole);
            }
        }
    }
}