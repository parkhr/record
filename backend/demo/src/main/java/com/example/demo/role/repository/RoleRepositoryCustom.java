package com.example.demo.role.repository;

import com.example.demo.role.request.SearchRoleRequest;
import com.example.demo.role.response.SearchRoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleRepositoryCustom {

    Page<SearchRoleResponse> findRoles(SearchRoleRequest request, Pageable pageable);
}
