package com.example.demo.economy.repository;

import com.example.demo.economy.entity.WalletLog;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface WalletLogRepository extends CrudRepository<WalletLog, Long> {

    List<WalletLog> findTop5ByAdminIdOrderByCreatedAtDesc(long id);
}
