package ru.vsu.csf.asashina.marketservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.asashina.marketservice.model.entity.Category;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Set<Category> findAllByCategoryIdIn(List<Long> ids);

    boolean existsCategoryByNameIgnoreCase(String name);
}
