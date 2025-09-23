package com.example.demo.economy.repository;

import com.example.demo.economy.entity.Word;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WordRepository extends CrudRepository<Word, Long>, WordRepositoryCustom {

    Optional<Word> findByAdminIdAndName(long id, String name);

    List<Word> findByAdminIdAndCompletedLessThan(long id, int completed);

    List<Word> findByAdminIdAndCompletedGreaterThanEqual(long id, int completed);

    @Query("SELECT * FROM word WHERE adminId = :adminId AND completed >= 5 ORDER BY RAND() LIMIT 100")
    List<Word> findRandomCompletedWords(@Param("adminId") Long adminId);
}
