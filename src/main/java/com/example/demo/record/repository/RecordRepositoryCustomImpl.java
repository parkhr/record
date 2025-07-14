package com.example.demo.record.repository;

import com.example.demo.record.repository.RecordRepositoryCustom;
import com.example.demo.record.request.SearchRecordRequest;
import com.example.demo.record.response.SearchRecordResponse;
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
        StringBuilder sql = new StringBuilder("SELECT title, content, status, createdAt, updatedAt FROM records WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM records WHERE 1=1");
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

        if (request.getStatus() != null) {
            sql.append(" AND status = :status");
            countSql.append(" AND status = :status");
            params.put("status", request.getStatus());
        }

        sql.append(" AND visibility = :visibility");
        countSql.append(" AND visibility = :visibility");
        params.put("visibility", "공개");

        sql.append(" ORDER BY id DESC");

        sql.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", pageable.getPageSize());
        params.put("offset", pageable.getPageNumber() * pageable.getPageSize());

        List<SearchRecordResponse> result = jdbc.query(sql.toString(), params, new BeanPropertyRowMapper<>(SearchRecordResponse.class));
        int total = jdbc.queryForObject(countSql.toString(), params, Integer.class);

        return new PageImpl<>(result, pageable, total);
    }


}
