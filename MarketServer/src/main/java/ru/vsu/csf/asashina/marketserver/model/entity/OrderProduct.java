package ru.vsu.csf.asashina.marketserver.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "order")
public class OrderProduct {

    @Id
    private String orderProductId;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_number")
    private Order order;

    public OrderProduct(String orderProductId, Integer amount, Product product) {
        this.orderProductId = orderProductId;
        this.amount = amount;
        this.product = product;
    }
}
