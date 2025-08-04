package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Active;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ActiveRepository extends CrudRepository<Active, Long>, ActiveRepositoryCustom {

    List<Active> findTop5ByAdminIdAndSavedTrueOrderByCreatedAtDesc(long id);
}
