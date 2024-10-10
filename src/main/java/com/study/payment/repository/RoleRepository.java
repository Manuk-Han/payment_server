package com.study.payment.repository;

import com.study.payment.common.UserRoles;
import com.study.payment.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByUserRoles(UserRoles userRoles);

    boolean existsRoleByUserRoles(UserRoles userRoles);
}
