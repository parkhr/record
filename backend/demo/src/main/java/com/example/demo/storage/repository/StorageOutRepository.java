package com.example.demo.storage.repository;

import com.example.demo.storage.entity.StorageOut;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StorageOutRepository extends CrudRepository<StorageOut, Long> {

    @Query("SELECT * FROM storage_out WHERE recordId = :recordId FOR UPDATE")
    List<StorageOut> findByRecordIdForUpdate(@Param("recordId") Long recordId);

    List<StorageOut> findByRecordId(Long recordId);
}
