package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Task;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByEpicId(long epicId);
}
