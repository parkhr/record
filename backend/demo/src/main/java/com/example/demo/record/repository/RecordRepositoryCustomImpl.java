package com.example.demo.record.repository;

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
public class RecordRepositoryCustomImpl implements RecordRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Page<SearchRecordResponse> findPublicRecords(SearchRecordRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT id, title, content, status, createdAt, updatedAt FROM records WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(1) FROM records WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            sql.append(" AND title LIKE :title");
            countSql.append(" AND title LIKE :title");
            params.put("title", "%" + request.getTitle() + "%");
        }

        if (request.getContent() != null && !request.getContent().isEmpty()) {
            sql.append(" AND content LIKE :content");
            countSql.append(" AND content LIKE :content");
            params.put("content", "%" + request.getContent() + "%");
        }

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            sql.append(" AND status = :status");
            countSql.append(" AND status = :status");
            params.put("status", request.getStatus());
        }

        if(request.getStartDate() != null && request.getEndDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            sql.append(" AND createdAt >= :startDate");
            countSql.append(" AND createdAt >= :startDate");
            params.put("startDate", startDateTime);

            sql.append(" AND createdAt <= :endDate");
            countSql.append(" AND createdAt >= :startDate");
            params.put("endDate", endDateTime);
        }

        sql.append(" AND isPublic = true");
        countSql.append(" AND isPublic = true");

        sql.append(" AND deletedAt IS NULL");
        countSql.append(" AND deletedAt IS NULL");


        sql.append(" ORDER BY id DESC");

        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<SearchRecordResponse> result = jdbc.query(sql.toString(), params, new BeanPropertyRowMapper<>(SearchRecordResponse.class));
        int total = jdbc.queryForObject(countSql.toString(), params, Integer.class);

        return new PageImpl<>(result, pageable, total);
    }


}
