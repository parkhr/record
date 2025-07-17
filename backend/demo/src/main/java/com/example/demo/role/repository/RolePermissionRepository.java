package com.example.demo.role.repository;

import com.example.demo.role.entity.RolePermission;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RolePermissionRepository extends CrudRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(long roleId);
}
