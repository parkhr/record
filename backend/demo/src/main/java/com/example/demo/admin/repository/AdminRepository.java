package com.example.demo.admin.repository;

import com.example.demo.admin.entity.Admin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByName(String username);

    @Query("SELECT DISTINCT a.* FROM admin a LEFT JOIN spend s ON a.id = s.adminId AND DATE(s.spendAt) = CURDATE() WHERE s.id IS NULL")
    List<Admin> findAdminsWithTodayNoSpend();

    @Query("SELECT DISTINCT a.* FROM admin a LEFT JOIN active ac ON a.id = ac.adminId AND DATE(ac.createdAt) = CURDATE() WHERE ac.id IS NULL")
    List<Admin> findAdminsWithTodayNoActive();
}