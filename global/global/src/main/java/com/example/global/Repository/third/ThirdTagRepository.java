package com.example.global.Repository.third;

import com.example.global.entity.TagTable;
import com.example.global.entity.third.Third;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdTagRepository extends JpaRepository<TagTable, Integer> {
        @Query("SELECT f FROM Third f JOIN f.tagTable t WHERE t.tagIndex = :tagIndex")
List<Third> findByTagIndex(@Param("tagIndex") Integer tagIndex);
}
