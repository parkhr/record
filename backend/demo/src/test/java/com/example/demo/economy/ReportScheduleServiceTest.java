package com.example.demo.economy;

import com.example.demo.admin.entity.Admin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
//@Transactional
class ReportScheduleServiceTest {

    @Autowired
    private ReportScheduleService reportScheduleService;

    @DisplayName("데일리리포트 설정")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void setReportConfig() {
//        CustomUserDetails fakeUser = new CustomUserDetails();
//        fakeUser.setId(1L);
//
//        // SecurityContextHolder에 Authentication 세팅
//        SecurityContextHolder.getContext().setAuthentication(
//            new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities())
//        );

        Admin admin = Admin.builder()
            .id(1L)
            .deletedAt(null)
            .build();

        reportScheduleService.createReport(admin);

    }
}