package com.example.demo.common.util;

import com.example.demo.common.CustomUserDetails;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static Optional<CustomUserDetails> getCustomUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }

        return Optional.empty();
    }
}
