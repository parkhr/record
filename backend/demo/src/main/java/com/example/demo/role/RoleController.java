package com.example.demo.role;

import com.example.demo.role.request.CreateRoleRequest;
import com.example.demo.role.request.SearchRoleRequest;
import com.example.demo.role.request.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasRole('WRITE_ROLE')")
    public ResponseEntity<Object> createRole(@RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PutMapping
    @PreAuthorize("hasRole('UPDATE_ROLE')")
    public ResponseEntity<Object> updateRole(@RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(request));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findPublicRoles(SearchRoleRequest request, Pageable pageable) {
        return ResponseEntity.ok(roleService.findRoles(request, pageable));
    }
}
