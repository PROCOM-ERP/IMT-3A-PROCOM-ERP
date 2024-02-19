package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT c FROM Category c WHERE c.id IN :ids")
    List<Category> findByIds(@Param("ids") List<Integer> ids);

    @Query("SELECT c FROM Category c WHERE c.title = :title")
    List<Category> findByTitle(@Param("title") String title);
}