package com.example.demo.menu.repository;

import com.example.demo.menu.entity.RoleMenu;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface RoleMenuRepository extends CrudRepository<RoleMenu, Long> {

    List<RoleMenu> findByRoleId(Long roleId);
}
