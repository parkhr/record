package com.example.demo.economy.repository;

import com.example.demo.economy.request.SearchWordRequest;
import com.example.demo.economy.response.SearchWordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WordRepositoryCustom {

    Page<SearchWordResponse> findWords(SearchWordRequest request, long adminId, Pageable pageable);
}
