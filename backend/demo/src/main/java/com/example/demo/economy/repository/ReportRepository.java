package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Report;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Report, Long> {

    Optional<Report> findByAdminIdAndDateBetween(Long adminId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
