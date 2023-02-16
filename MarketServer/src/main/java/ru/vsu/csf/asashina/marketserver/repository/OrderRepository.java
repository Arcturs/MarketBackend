package ru.vsu.csf.asashina.marketserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.asashina.marketserver.model.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = """
                    SELECT o
                    FROM Order o
                    JOIN o.user u
                      ON u.userId = :userId""")
    Page<Order> getAllInPagesByUserId(@Param("userId") Long userId, Pageable pageable);
}
