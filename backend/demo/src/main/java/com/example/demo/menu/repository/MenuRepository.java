package com.example.demo.menu.repository;

import com.example.demo.menu.entity.Menu;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MenuRepository extends CrudRepository<Menu, Long> {

    List<Menu> findByIdIn(List<Long> menuIds);
}
