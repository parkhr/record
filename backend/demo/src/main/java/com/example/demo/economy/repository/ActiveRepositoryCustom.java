package com.example.demo.economy.repository;

import com.example.demo.economy.request.SearchActiveRequest;
import com.example.demo.economy.request.SearchSpendRequest;
import com.example.demo.economy.response.SearchActiveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActiveRepositoryCustom {

    Page<SearchActiveResponse> findActives(SearchActiveRequest request, long adminId, Pageable pageable);
}
