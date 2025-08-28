package com.example.demo.economy.repository;

import com.example.demo.economy.entity.WordLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface WordLogRepository extends CrudRepository<WordLog, Long> {

    List<WordLog> findByAdminIdAndCreatedAtBetween(long adminId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
