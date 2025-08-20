package com.example.demo.economy.repository;

import com.example.demo.economy.response.EpicResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EpicRepositoryCustomImpl implements EpicRepositoryCustom {


    @Override
    public List<EpicResponse> findTodo(long adminId) {

        StringBuilder sql = new StringBuilder("SELECT * FROM epic WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        sql.append(" AND adminId =:adminId");
        params.put("adminId", adminId);

        //TODO 조회

        return null;
    }
}
