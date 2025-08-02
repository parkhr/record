package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Active;
import org.springframework.data.repository.CrudRepository;

public interface ActiveRepository extends CrudRepository<Active, Long>, ActiveRepositoryCustom {

}
