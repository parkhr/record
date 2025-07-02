package com.example.demo.repository;

import com.example.demo.entity.Records;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Records, Long>, RecordRepositoryCustom {

}
