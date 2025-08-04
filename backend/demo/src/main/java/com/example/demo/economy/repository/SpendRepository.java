package com.example.demo.economy.repository;

import com.example.demo.admin.entity.Admin;
import com.example.demo.economy.entity.Spend;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SpendRepository extends CrudRepository<Spend, Long>, SpendRepositoryCustom {

    List<Spend> findTop5ByAdminIdAndDeductedTrueOrderBySpendAtDesc(long id);

    List<Spend> findByAdminIdAndDeductedTrueAndSpendAtBetween(Admin admin, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
