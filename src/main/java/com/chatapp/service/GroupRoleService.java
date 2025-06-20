package com.chatapp.service;

import com.chatapp.repository.GroupRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRoleService {
    private final GroupRoleRepository groupRoleRepository;

    @Autowired
    public GroupRoleService(GroupRoleRepository groupRoleRepository) {
        this.groupRoleRepository = groupRoleRepository;
    }
}
