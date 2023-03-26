package ru.vsu.csf.asashina.marketservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.csf.asashina.marketservice.model.entity.Order;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(value = """
            SELECT o
            FROM Order o
            JOIN o.user u
              ON u.userId = :userId""")
    Page<Order> getAllInPagesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
            SELECT o
            FROM Order o
            JOIN o.user u
              ON u.userId = :userId
            WHERE o.isPaid = false
            ORDER BY o.created DESC
            LIMIT 1""")
    Optional<Order> findLastNotPaidOrderByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = """
                INSERT INTO order_product(order_product_id, order_number, product_id, amount)
                VALUES (:id, :orderNumber, :productId, :amount)""", nativeQuery = true)
    void addProductToOrder(@Param("id") String id,
                           @Param("orderNumber") String orderNumber,
                           @Param("productId") Long productId,
                           @Param("amount") Integer amount);

    @Modifying
    @Query(value = """
                UPDATE order_product
                SET amount = :amount
                WHERE order_number = :orderNumber
                  AND product_id = :productId""", nativeQuery = true)
    void updateProductAmountInOrder(@Param("orderNumber") String orderNumber,
                                    @Param("productId") Long productId,
                                    @Param("amount") Integer amount);

    @Modifying
    @Query(value = """
                DELETE FROM order_product
                WHERE order_number = :orderNumber
                  AND product_id = :productId""", nativeQuery = true)
    void deleteProductFromOrder(@Param("orderNumber") String orderNumber,
                                @Param("productId") Long productId);

    @Modifying
    @Query(value = """
                UPDATE order_info
                SET is_paid = true
                WHERE order_number = :orderNumber""", nativeQuery = true)
    void setOrderPaid(@Param("orderNumber") String orderNumber);
}
