package com.example.demo.economy.repository;

import com.example.demo.economy.request.SearchSpendRequest;
import com.example.demo.record.repository.RecordRepositoryCustom;
import com.example.demo.record.request.SearchRecordRequest;
import com.example.demo.record.response.SearchRecordResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SpendRepositoryCustomImpl implements SpendRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT id, amount, place, deducted, spendAt, createdAt FROM spend WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(1) FROM spend WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        sql.append(" AND deletedAt IS NULL");
        countSql.append(" AND deletedAt IS NULL");

//        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
//            sql.append(" AND status = :status");
//            countSql.append(" AND status = :status");
//            params.put("status", request.getStatus());
//        }

        sql.append(" ORDER BY id DESC");

        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<SearchSpendResponse> result = jdbc.query(sql.toString(), params, new BeanPropertyRowMapper<>(SearchSpendResponse.class));
        int total = jdbc.queryForObject(countSql.toString(), params, Integer.class);

        return new PageImpl<>(result, pageable, total);
    }
}
