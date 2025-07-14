package com.example.demo.role.repository;

import com.example.demo.role.entity.Permission;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, Long> {

    List<Permission> findByIdIn(List<Long> permissionIds);
}
