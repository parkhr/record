package com.example.demo.role.repository;

import com.example.demo.role.entity.Role;
import com.example.demo.role.request.SearchRoleRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long>, RoleRepositoryCustom {

}
