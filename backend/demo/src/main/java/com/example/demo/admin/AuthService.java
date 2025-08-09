package com.example.demo.admin;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.entity.LoginLog;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.admin.repository.LoginLogRepository;
import com.example.demo.common.JwtTokenProvider;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.menu.repository.MenuRepository;
import com.example.demo.menu.repository.RoleMenuRepository;
import com.example.demo.role.entity.Permission;
import com.example.demo.role.entity.Role;
import com.example.demo.role.entity.RolePermission;
import com.example.demo.enums.PermissionType;
import com.example.demo.role.repository.PermissionRepository;
import com.example.demo.role.repository.RolePermissionRepository;
import com.example.demo.role.repository.RoleRepository;
import com.example.demo.admin.request.LoginRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String login(LoginRequest request) {
        Admin admin = adminRepository.findByName(request.getName()).orElseThrow(() -> new BadCredentialsException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(ADMIN_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        Role role = roleRepository.findById(admin.getRoleId()).orElseThrow(() -> new BadCredentialsException("해당하는 권한그룹이 없습니다."));

        if (role.isDeleted()) {
            throw new ApplicationException("해당하는 권한그룹이 없습니다.");
        }

        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream().filter(item -> !item.isDeleted())
            .map(RolePermission::getPermissionId).toList();

        if (permissionIds.isEmpty()) {
            throw new BadCredentialsException("해당하는 권한이 없습니다.");
        }

        List<Permission> permissions = permissionRepository.findByIdIn(permissionIds);
        if (permissions.isEmpty()) {
            throw new BadCredentialsException("해당하는 권한이 없습니다.");
        }

        List<PermissionType> permissionTypes = permissions.stream().filter(item -> !item.isDeleted()).map(Permission::getName).toList();
        
        loginLogRepository.save(LoginLog.createLoginLog(admin.getId()));

        return jwtTokenProvider.createToken(admin.getId(), admin.getName(), permissionTypes, role.getId());
    }
}
