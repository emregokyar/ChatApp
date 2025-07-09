package com.chatapp.service;

import com.chatapp.entity.GroupRole;
import com.chatapp.repository.GroupRoleRepository;
import com.chatapp.util.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRoleService {
    private final GroupRoleRepository groupRoleRepository;

    @Autowired
    public GroupRoleService(GroupRoleRepository groupRoleRepository) {
        this.groupRoleRepository = groupRoleRepository;
    }

    public GroupRole saveNewRole(GroupRole role) {
        return groupRoleRepository.save(role);
    }

    public GroupRole getAdminRole() {
        return groupRoleRepository.findByRole(Roles.ADMIN)
                .orElseThrow(() -> new RuntimeException("Can not find a role associated with this name"));
    }

    public GroupRole getRegularRole() {
        return groupRoleRepository.findByRole(Roles.REGULAR)
                .orElseThrow(() -> new RuntimeException("Can not find a role associated with this name"));
    }
}
