package com.example.demo.layer.repository;

import com.example.demo.layer.entity.Series;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SeriesRepository extends CrudRepository<Series, Long> {

    List<Series> findByCollectionId(long collectionId);
}
