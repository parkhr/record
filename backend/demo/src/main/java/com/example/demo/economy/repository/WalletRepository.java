package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Wallet;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

    Optional<Wallet> findByAdminId(long id);
}
