package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {

}
