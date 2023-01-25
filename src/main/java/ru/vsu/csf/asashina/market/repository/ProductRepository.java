package ru.vsu.csf.asashina.market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.asashina.market.model.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
