package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Epic;
import org.springframework.data.repository.CrudRepository;

public interface EpicRepository extends CrudRepository<Epic, Long>, EpicRepositoryCustom {

}