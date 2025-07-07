package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;
import com.example.demo.request.CreateAdminRequest;
import com.example.demo.request.UpdateAdminRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("관리자 생성 성공")
    @Test
    void createAdmin() {
        //given
        CreateAdminRequest request = new CreateAdminRequest();
        request.setName("관리자이름");
        request.setPassword("12345678");
        request.setAuthGroup("auth");

        //when
        Admin admin = adminService.createAdmin(request);

        //then
        passwordEncoder.matches(request.getPassword(), admin.getPassword());
        assertThat(admin.getName()).isEqualTo("관리자이름");
        assertTrue(passwordEncoder.matches("12345678", admin.getPassword()));
        assertThat(admin.getAuthGroup()).isEqualTo("auth");
        assertNotNull(admin.getCreatedAt());
    }

    @DisplayName("관리자 수정 성공")
    @Test
    void updateAdmin() {
        //given
        CreateAdminRequest request = new CreateAdminRequest();
        request.setName("관리자이름");
        request.setPassword("12345678");
        request.setAuthGroup("auth");

        Admin admin = adminService.createAdmin(request);

        UpdateAdminRequest request2 = new UpdateAdminRequest();
        request2.setIsUse(false);
        request2.setAdminId(admin.getId());
        request2.setAuthGroup("auth");

        //when
        Admin updatedAdmin = adminService.update(request2);

        //then
        assertThat(updatedAdmin.getName()).isEqualTo("관리자이름");
        assertThat(updatedAdmin.getAuthGroup()).isEqualTo("auth");
        assertFalse(updatedAdmin.isUse());
        assertNotNull(updatedAdmin.getUpdatedAt());
    }

    @DisplayName("관리자 수정 실패 (관리자가 없는 경우)")
    @Test
    void updateAdmin_fail() {
        //given

        UpdateAdminRequest request2 = new UpdateAdminRequest();
        request2.setIsUse(false);
        request2.setAdminId(0L);
        request2.setAuthGroup("auth");

        //when
        assertThatThrownBy(() -> adminService.update(request2)).isInstanceOf(ApplicationException.class).hasMessage(ADMIN_NOT_FOUND);
    }

    @DisplayName("관리자 삭제 성공")
    @Test
    void deleteAdmin() {
        //given
        CreateAdminRequest request = new CreateAdminRequest();
        request.setName("관리자이름");
        request.setPassword("12345678");
        request.setAuthGroup("auth");

        Admin admin = adminService.createAdmin(request);

        adminService.delete(admin.getId());
        Optional<Admin> deletedAdmin = adminRepository.findById(admin.getId());

        // then
        assertTrue(deletedAdmin.isPresent());
        assertNotNull(deletedAdmin.get().getDeletedAt());
    }

    @DisplayName("관리자 삭제 실패 (관리자가 없는 경우)")
    @Test
    void deleteAdmin_fail1() {

        assertThatThrownBy(() -> adminService.delete(0L)).isInstanceOf(ApplicationException.class).hasMessage(ADMIN_NOT_FOUND);
    }

    @DisplayName("관리자 패스워드 초기화 성공")
    @Test
    void resetPassword() {
        //given
        CreateAdminRequest request = new CreateAdminRequest();
        request.setName("관리자이름");
        request.setPassword("12345678");
        request.setAuthGroup("auth");

        Admin admin = adminService.createAdmin(request);

        adminService.resetPassword(admin.getId());
        Optional<Admin> updatedAdmin = adminRepository.findById(admin.getId());

        // then
        assertTrue(updatedAdmin.isPresent());
        assertThat(updatedAdmin.get().getPassword()).isNotEqualTo("12345678");
        assertNotNull(updatedAdmin.get().getUpdatedAt());
    }

    @DisplayName("패스워드초기화 실패 (관리자가 없는 경우)")
    @Test
    void resetPassword_fail() {
        //when
        assertThatThrownBy(() -> adminService.resetPassword(0L)).isInstanceOf(ApplicationException.class).hasMessage(ADMIN_NOT_FOUND);
    }
}