package com.example.demo.reserve.repository;

import com.example.demo.reserve.entity.Reserve;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ReserveRepository extends CrudRepository<Reserve, Long> {

    List<Reserve> findByRecordIdAndUserId(Long recordId, Long userId);
}
