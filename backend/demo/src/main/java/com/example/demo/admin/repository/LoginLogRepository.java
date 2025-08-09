package com.example.demo.admin.repository;

import com.example.demo.admin.entity.LoginLog;
import org.springframework.data.repository.CrudRepository;

public interface LoginLogRepository extends CrudRepository<LoginLog, Long> {

}