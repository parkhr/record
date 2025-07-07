package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.PERMISSION_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.ROLE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.request.CreatePermissionRequest;
import com.example.demo.request.CreateRoleRequest;
import com.example.demo.request.UpdatePermissionRequest;
import com.example.demo.request.UpdateRoleRequest;
import java.util.List;
import java.util.Optional;
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

        UpdateRoleRequest request2 = new UpdateRoleRequest();
        request2.setId(role.getId());
        request2.setContent("수정내용");
        request2.setPermissionIds(List.of(1L, 2L, 3L, 4L, 5L));

        //when
        Role updatedRole = roleService.updateRole(request2);

        //then
        assertThat(updatedRole.getName()).isEqualTo("권한그룹이름");
        assertThat(updatedRole.getContent()).isEqualTo("수정내용");
        assertNotNull(updatedRole.getUpdatedAt());
    }

    @DisplayName("권한그룹 수정 실패 (권한그룹이 없는 경우)")
    @Test
    void updateRole_fail() {
        //given

        UpdateRoleRequest request2 = new UpdateRoleRequest();
        request2.setId(0L);
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
        request.setName("권한이름");
        request.setContent("권한내용");

        //when
        Permission permission = roleService.createPermission(request);

        //then
        assertThat(permission.getName()).isEqualTo("권한이름");
        assertThat(permission.getContent()).isEqualTo("권한내용");
        assertNotNull(permission.getCreatedAt());
    }

    @DisplayName("권한 수정 성공")
    @Test
    void updatePermission() {
        //given
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setName("권한이름");
        request.setContent("권한내용");

        Permission permission = roleService.createPermission(request);

        UpdatePermissionRequest request2 = new UpdatePermissionRequest();
        request2.setId(permission.getId());
        request2.setContent("수정내용");

        //when
        Permission updatedPermission = roleService.updatePermission(request2);

        //then
        assertThat(updatedPermission.getName()).isEqualTo("권한이름");
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
        request.setName("권한이름");
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