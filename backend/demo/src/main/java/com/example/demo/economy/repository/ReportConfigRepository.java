package com.example.demo.economy.repository;

import com.example.demo.economy.entity.ReportConfig;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ReportConfigRepository extends CrudRepository<ReportConfig, Long> {

    List<ReportConfig> findByAdminId(long id);
}