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
    public Page<SearchSpendResponse> findSpends(SearchSpendRequest request, long adminId, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT id, amount, place, deducted, spendAt, createdAt FROM spend WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(1) FROM spend WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        sql.append(" AND adminId =:adminId");
        countSql.append(" AND adminId =:adminId");
        params.put("adminId", adminId);

        sql.append(" AND deletedAt IS NULL");
        countSql.append(" AND deletedAt IS NULL");

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            sql.append(" AND deducted = :status");
            countSql.append(" AND deducted = :status");
            params.put("status", request.getStatus());
        }

        if(request.getStartDate() != null && request.getStartDate().isEmpty() && request.getEndDate() != null && request.getEndDate().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            sql.append(" AND spendAt >= :startDate");
            countSql.append(" AND spendAt >= :startDate");
            params.put("startDate", startDateTime);

            sql.append(" AND spendAt <= :endDate");
            countSql.append(" AND spendAt >= :startDate");
            params.put("endDate", endDateTime);
        }

        sql.append(" ORDER BY spendAt DESC");

        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<SearchSpendResponse> result = jdbc.query(sql.toString(), params, new BeanPropertyRowMapper<>(SearchSpendResponse.class));
        int total = jdbc.queryForObject(countSql.toString(), params, Integer.class);

        return new PageImpl<>(result, pageable, total);
    }
}
