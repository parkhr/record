package com.example.demo.service;

import com.example.demo.common.JwtTokenProvider;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RolePermission;
import com.example.demo.enums.PermissionType;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RolePermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.request.LoginRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequest request) {
        Admin admin = adminRepository.findByName(request.getName()).orElseThrow(() -> new ApplicationException("해당하는 유저가 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Role role = roleRepository.findById(admin.getRoleId()).orElseThrow(() -> new ApplicationException("해당하는 권한그룹이 없습니다."));

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(role.getId());
        if (rolePermissions.isEmpty()) {
            throw new ApplicationException("해당하는 권한이 없습니다.");
        }
        List<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).toList();

        List<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        if (permissions.isEmpty()) {
            throw new ApplicationException("해당하는 권한이 없습니다.");
        }
        List<PermissionType> permissionTypes = permissions.stream().map(Permission::getName).toList();

        return jwtTokenProvider.createToken(admin.getName(), permissionTypes);
    }
}
