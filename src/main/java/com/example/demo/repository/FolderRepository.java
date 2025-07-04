package com.example.demo.repository;

import com.example.demo.entity.Folder;
import com.example.demo.entity.Series;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface FolderRepository extends CrudRepository<Folder, Long> {

    List<Folder> findBySeriesId(long seriesId);
}
