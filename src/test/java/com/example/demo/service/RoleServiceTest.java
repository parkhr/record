package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.PERMISSION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.ROLE_NOT_FOUND;
import static com.example.demo.enums.PermissionType.DELETE_RECORD;
import static com.example.demo.enums.PermissionType.READ_RECORD;
import static com.example.demo.enums.PermissionType.UPDATE_RECORD;
import static com.example.demo.enums.PermissionType.WRITE_RECORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.role.entity.Permission;
import com.example.demo.role.entity.Role;
import com.example.demo.role.entity.RolePermission;
import com.example.demo.role.repository.PermissionRepository;
import com.example.demo.role.repository.RolePermissionRepository;
import com.example.demo.role.repository.RoleRepository;
import com.example.demo.role.request.CreatePermissionRequest;
import com.example.demo.role.request.CreateRoleRequest;
import com.example.demo.role.request.UpdatePermissionRequest;
import com.example.demo.role.request.UpdateRoleRequest;
import com.example.demo.role.RoleService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @DisplayName("권한그룹 생성 성공")
    @Test
    void createRole() {
        //given
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("권한그룹이름");
        request.setContent("권한그룹내용");

        //when
        Role role = roleService.createRole(request);

        //then
        assertThat(role.getName()).isEqualTo("권한그룹이름");
        assertThat(role.getContent()).isEqualTo("권한그룹내용");
        assertNotNull(role.getCreatedAt());
    }

    @DisplayName("권한그룹 수정 성공")
    @Test
    void updateRole() {
        //given
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("권한그룹이름");
        request.setContent("권한그룹내용");

        Role role = roleService.createRole(request);

        CreatePermissionRequest request3 = new CreatePermissionRequest();
        request3.setContent("권한 내용");
        request3.setName(WRITE_RECORD);

        Permission permission = roleService.createPermission(request3);
        request3.setName(READ_RECORD);
        Permission permission2 = roleService.createPermission(request3);
        request3.setName(UPDATE_RECORD);
        Permission permission3 = roleService.createPermission(request3);
        request3.setName(DELETE_RECORD);
        Permission permission4 = roleService.createPermission(request3);

        UpdateRoleRequest request2 = new UpdateRoleRequest();
        request2.setRoleId(role.getId());
        request2.setContent("수정내용");
        request2.setPermissionIds(List.of(permission.getId(), permission2.getId(), permission3.getId(), permission4.getId()));

        //when
        Role updatedRole = roleService.updateRole(request2);
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(updatedRole.getId());

        //then
        assertThat(updatedRole.getName()).isEqualTo("권한그룹이름");
        assertThat(updatedRole.getContent()).isEqualTo("수정내용");
        assertNotNull(updatedRole.getUpdatedAt());

        assertThat(rolePermissions.size()).isEqualTo(4);
        assertThat(rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList())).containsExactlyInAnyOrder(
            permission.getId(), permission2.getId(), permission3.getId(), permission4.getId());


        //when
        request2.setPermissionIds(List.of(permission.getId()));
        roleService.updateRole(request2);
        rolePermissions = rolePermissionRepository.findByRoleId(updatedRole.getId());

        //then
        assertThat(rolePermissions.size()).isEqualTo(1);
        assertThat(rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toList())).containsExactlyInAnyOrder(
            permission.getId());
    }

    @DisplayName("권한그룹 수정 실패 (권한그룹이 없는 경우)")
    @Test
    void updateRole_fail() {
        //given

        UpdateRoleRequest request2 = new UpdateRoleRequest();
        request2.setRoleId(0L);
        request2.setContent("권한그룹내용");

        //when
        assertThatThrownBy(() -> roleService.updateRole(request2)).isInstanceOf(ApplicationException.class).hasMessage(ROLE_NOT_FOUND);
    }

    @DisplayName("권한그룹 삭제 성공")
    @Test
    void deleteRole() {
        //given
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("권한그룹이름");
        request.setContent("권한그룹내용");

        Role role = roleService.createRole(request);

        roleService.deleteRole(role.getId());
        Optional<Role> deletedAdmin = roleRepository.findById(role.getId());

        // then
        assertTrue(deletedAdmin.isPresent());
        assertNotNull(deletedAdmin.get().getDeletedAt());
    }

    @DisplayName("권한그룹 삭제 실패 (권한그룹이 없는 경우)")
    @Test
    void deleteRole_fail1() {

        assertThatThrownBy(() -> roleService.deleteRole(0L)).isInstanceOf(ApplicationException.class).hasMessage(ROLE_NOT_FOUND);
    }


    @DisplayName("권한 생성 성공")
    @Test
    void createPermission() {
        //given
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setName(WRITE_RECORD);
        request.setContent("권한내용");

        //when
        Permission permission = roleService.createPermission(request);

        //then
        assertThat(permission.getName()).isEqualTo(WRITE_RECORD);
        assertThat(permission.getContent()).isEqualTo("권한내용");
        assertNotNull(permission.getCreatedAt());
    }

    @DisplayName("권한 수정 성공")
    @Test
    void updatePermission() {
        //given
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setName(WRITE_RECORD);
        request.setContent("권한내용");

        Permission permission = roleService.createPermission(request);

        UpdatePermissionRequest request2 = new UpdatePermissionRequest();
        request2.setId(permission.getId());
        request2.setContent("수정내용");

        //when
        Permission updatedPermission = roleService.updatePermission(request2);

        //then
        assertThat(updatedPermission.getName()).isEqualTo(WRITE_RECORD);
        assertThat(updatedPermission.getContent()).isEqualTo("수정내용");
        assertNotNull(updatedPermission.getUpdatedAt());
    }

    @DisplayName("권한 수정 실패 (권한이 없는 경우)")
    @Test
    void updatePermission_fail() {
        //given

        UpdatePermissionRequest request2 = new UpdatePermissionRequest();
        request2.setId(0L);
        request2.setContent("권한내용");

        //when
        assertThatThrownBy(() -> roleService.updatePermission(request2)).isInstanceOf(ApplicationException.class).hasMessage(PERMISSION_NOT_FOUND);
    }

    @DisplayName("권한 삭제 성공")
    @Test
    void deletePermission() {
        //given
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setName(WRITE_RECORD);
        request.setContent("권한내용");

        Permission permission = roleService.createPermission(request);

        roleService.deletePermission(permission.getId());
        Optional<Permission> deletedAdmin = permissionRepository.findById(permission.getId());

        // then
        assertTrue(deletedAdmin.isPresent());
        assertNotNull(deletedAdmin.get().getDeletedAt());
    }

    @DisplayName("권한 삭제 실패 (권한이 없는 경우)")
    @Test
    void deletePermission_fail1() {

        assertThatThrownBy(() -> roleService.deletePermission(0L)).isInstanceOf(ApplicationException.class).hasMessage(PERMISSION_NOT_FOUND);
    }
}