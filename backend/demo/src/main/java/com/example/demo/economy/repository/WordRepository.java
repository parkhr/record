package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Word;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface WordRepository extends CrudRepository<Word, Long>, WordRepositoryCustom {

    Optional<Word> findByAdminIdAndName(long id, String name);

    List<Word> findByAdminIdAndCompletedIsTrue(long id);

    List<Word> findByAdminIdAndCompletedIsFalse(long id);
}
