package ru.vsu.csf.asashina.marketserver.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class OrderProduct {

    @Id
    private String orderProductId;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;
}
