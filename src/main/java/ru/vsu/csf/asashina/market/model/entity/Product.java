package ru.vsu.csf.asashina.market.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "product")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private Integer amount;
}
