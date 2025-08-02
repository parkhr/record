package com.example.demo.economy.repository;

import com.example.demo.economy.request.SearchSpendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpendRepositoryCustom {

    Page<SearchSpendResponse> findSpends(SearchSpendRequest request, long adminId, Pageable pageable);
}
