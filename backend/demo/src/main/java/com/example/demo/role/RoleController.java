package com.example.demo.role;

import com.example.demo.role.request.SearchRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/search")
    public ResponseEntity<Object> findPublicRoles(SearchRoleRequest request, Pageable pageable) {
        return ResponseEntity.ok(roleService.findRoles(request, pageable));
    }
}
