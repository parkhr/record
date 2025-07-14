package com.example.demo.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.common.JwtAuthFilter;
import com.example.demo.common.config.SecurityConfig;
import com.example.demo.record.RecordController;
import com.example.demo.record.request.CreateRecordRequest;
import com.example.demo.record.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(controllers = RecordController.class)
@Import({SecurityConfig.class}) // JwtAuthFilter, TokenProvider도 필요시 수동 주입
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecordService recordService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter; // 실제 필터는 mocking 처리

    @Test
    void 권한_없으면_403() throws Exception {
        // 필터에서 인증 없이 통과시킴 (즉 인증된 사용자 없음)
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, FilterChain.class)
                .doFilter(invocation.getArgument(0, HttpServletRequest.class), invocation.getArgument(1, HttpServletResponse.class));
            return null;
        }).when(jwtAuthFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
                post("/api/record")
                    .with(csrf())
                    .content(objectMapper.writeValueAsString(new CreateRecordRequest()))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void WRITE_RECORD_권한_있으면_200() throws Exception {
        // SecurityContext에 ROLE_WRITE_RECORD 세팅
        Mockito.doAnswer(invocation -> {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_WRITE_RECORD"));
            Authentication auth = new UsernamePasswordAuthenticationToken("hongryul", null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            invocation.getArgument(2, FilterChain.class)
                .doFilter(invocation.getArgument(0, HttpServletRequest.class), invocation.getArgument(1, HttpServletResponse.class));
            return null;
        }).when(jwtAuthFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(
            post("/api/record")
                .with(csrf())
                .content(objectMapper.writeValueAsString(new CreateRecordRequest()))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}