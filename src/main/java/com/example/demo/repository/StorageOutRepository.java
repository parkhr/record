package com.example.demo.repository;

import com.example.demo.entity.StorageOut;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface StorageOutRepository extends CrudRepository<StorageOut, Long> {

    Optional<StorageOut> findByRecordIdAndDeletedAtIsNull(long recordId);
}
