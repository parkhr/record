package com.example.demo.layer.repository;

import com.example.demo.layer.entity.Folder;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface FolderRepository extends CrudRepository<Folder, Long> {

    List<Folder> findBySeriesId(long seriesId);
}
