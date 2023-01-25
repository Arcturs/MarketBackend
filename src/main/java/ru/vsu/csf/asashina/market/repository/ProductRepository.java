package ru.vsu.csf.asashina.market.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.asashina.market.model.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
                    SELECT *
                    FROM product
                    WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')
                    """, nativeQuery = true)
    Page<Product> getProductInPagesAndSearchByName(@Param("name") String name, Pageable pageable);
}
