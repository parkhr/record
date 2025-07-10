package com.example.demo.repository;

import com.example.demo.entity.Admin;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByName(String username);
}
