package com.example.global.Repository.second;

import com.example.global.entity.TagTable;
import com.example.global.entity.second.Secondary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondaryTagRepository extends JpaRepository<TagTable, Integer> {
    @Query("SELECT f FROM Secondary f JOIN f.tagTable t WHERE t.tagIndex = :tagIndex")
List<Secondary> findByTagIndex(@Param("tagIndex") Integer tagIndex);
}
