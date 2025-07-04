package com.example.demo.repository;

import com.example.demo.entity.Series;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SeriesRepository extends CrudRepository<Series, Long> {

    List<Series> findByCollectionId(long collectionId);
}
