package com.example.demo.record.repository;

import com.example.demo.record.request.SearchRecordRequest;
import com.example.demo.record.response.SearchRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordRepositoryCustom {

    Page<SearchRecordResponse> findPublicRecords(SearchRecordRequest request, Pageable pageable);
}
