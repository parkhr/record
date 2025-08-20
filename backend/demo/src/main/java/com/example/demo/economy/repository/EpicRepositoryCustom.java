package com.example.demo.economy.repository;

import com.example.demo.economy.response.EpicResponse;
import java.util.List;

public interface EpicRepositoryCustom {

    List<EpicResponse> findTodo(long adminId);
}
