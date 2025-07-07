package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;
import com.example.demo.request.CreateAdminRequest;
import com.example.demo.request.UpdateAdminRequest;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Admin createAdmin(CreateAdminRequest request) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Admin admin = Admin.createAdmin(request);

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin update(UpdateAdminRequest request) {

        Admin admin = adminRepository.findById(request.getAdminId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        admin.update(request);

        return adminRepository.save(admin);
    }

    @Transactional
    public void delete(long adminId) {

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        admin.delete();

        adminRepository.save(admin);
    }

    @Transactional
    public void resetPassword(long adminId) {

        //TODO 비밀번호 난수 생성
        String newPassword = generateRandomPassword();

        // encode
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        admin.resetPassword(encodedNewPassword);

        adminRepository.save(admin);
    }

    // 난수 비밀번호 생성기 (영문 + 숫자 조합)
    private String generateRandomPassword() {

        final int length = 8;
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom(); // 보안용 난수 생성기
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }

        return sb.toString();
    }
}
