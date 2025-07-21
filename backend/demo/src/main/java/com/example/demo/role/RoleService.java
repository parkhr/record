package com.example.demo.role;

import static com.example.demo.common.ErrorMessage.PERMISSION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.ROLE_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.role.entity.Permission;
import com.example.demo.role.entity.Role;
import com.example.demo.role.entity.RolePermission;
import com.example.demo.role.repository.PermissionRepository;
import com.example.demo.role.repository.RolePermissionRepository;
import com.example.demo.role.repository.RoleRepository;
import com.example.demo.role.request.CreatePermissionRequest;
import com.example.demo.role.request.CreateRoleRequest;
import com.example.demo.role.request.SearchRoleRequest;
import com.example.demo.role.request.UpdatePermissionRequest;
import com.example.demo.role.request.UpdateRoleRequest;
import com.example.demo.role.response.SearchRoleResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public Role createRole(CreateRoleRequest request) {

        return roleRepository.save(Role.createTempRole(request));
    }

    @Transactional
    public Role updateRole(UpdateRoleRequest request) {

        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new ApplicationException(ROLE_NOT_FOUND));

        if (role.isDeleted()) {
            throw new ApplicationException(ROLE_NOT_FOUND);
        }

        role.update(request);

        rolePermission(request, role);

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ApplicationException(ROLE_NOT_FOUND));

        if (role.isDeleted()) {
            throw new ApplicationException(ROLE_NOT_FOUND);
        }

        role.delete();

        //TODO delete rolePermission
        rolePermissionRepository.findByRoleId(roleId);

        roleRepository.save(role);
    }

    @Transactional
    public Permission createPermission(CreatePermissionRequest request) {

        return permissionRepository.save(Permission.createTempPermission(request));
    }

    @Transactional
    public Permission updatePermission(UpdatePermissionRequest request) {

        Permission permission = permissionRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(PERMISSION_NOT_FOUND));

        if (permission.isDeleted()) {
            throw new ApplicationException(PERMISSION_NOT_FOUND);
        }

        permission.update(request);

        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(long permissionId) {
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ApplicationException(PERMISSION_NOT_FOUND));

        if (permission.isDeleted()) {
            throw new ApplicationException(PERMISSION_NOT_FOUND);
        }

        permission.delete();

        permissionRepository.save(permission);
    }

    private void rolePermission(UpdateRoleRequest request, Role role) {
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {

            List<Long> permissionIds = request.getPermissionIds();
            List<Permission> permissions = permissionRepository.findByIdIn(permissionIds).stream().filter(item -> !item.isDeleted()).toList();

            if (permissions.size() != permissionIds.size()) {
                throw new ApplicationException("존재하지 않은 권한이 있습니다.");
            }

            List<RolePermission> existingRolePermissions = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .filter(item -> !item.isDeleted()).toList();

            Map<Long, RolePermission> existingPermissionMap = new HashMap<>();
            for (RolePermission item : existingRolePermissions) {
                existingPermissionMap.put(item.getPermissionId(), item);
            }

            // 등록 대상 = 요청에 있지만 기존에 없는 권한 ID
            for (long permissionId : permissionIds) {
                if (!existingPermissionMap.containsKey(permissionId)) {
                    rolePermissionRepository.save(RolePermission.builder()
                        .roleId(role.getId())
                        .permissionId(permissionId)
                        .createdAt(LocalDateTime.now())
                        .build());
                }
            }

            // 삭제 대상 = 기존에는 있었지만, 요청에 없는 권한 ID
            for (RolePermission existing : existingRolePermissions) {
                if (!permissionIds.contains(existing.getPermissionId())) {
                    rolePermissionRepository.delete(existing);
                }
            }
        }
    }

    public Page<SearchRoleResponse> findRoles(SearchRoleRequest request, Pageable pageable) {
        return roleRepository.findRoles(request, pageable);
    }
}
