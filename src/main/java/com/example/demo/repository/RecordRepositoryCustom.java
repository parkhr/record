package com.example.demo.repository;

import com.example.demo.request.SearchRecordRequest;
import com.example.demo.response.SearchRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecordRepositoryCustom {

    Page<SearchRecordResponse> findPublicRecords(SearchRecordRequest request, Pageable pageable);
}
