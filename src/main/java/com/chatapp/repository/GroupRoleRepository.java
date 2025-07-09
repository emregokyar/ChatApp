package com.chatapp.repository;

import com.chatapp.entity.GroupRole;
import com.chatapp.util.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRoleRepository extends JpaRepository<GroupRole, Integer> {
    Optional<GroupRole> findByRole(Roles role);
}
