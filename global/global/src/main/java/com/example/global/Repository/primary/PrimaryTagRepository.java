package com.example.global.Repository.primary;

import com.example.global.entity.TagTable;
import com.example.global.entity.primary.Primary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrimaryTagRepository extends JpaRepository<TagTable, Integer> {
    @Query("SELECT f FROM Primary f JOIN f.tagTable t WHERE t.tagIndex = :tagIndex")
List<Primary> findByTagIndex(@Param("tagIndex") Integer tagIndex);

}
