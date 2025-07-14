package com.example.demo.record.repository;

import com.example.demo.record.entity.Records;
import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Records, Long>, RecordRepositoryCustom {

}
