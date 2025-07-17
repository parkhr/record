package com.example.demo.admin.repository;

import com.example.demo.admin.entity.Admin;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByName(String username);
}
