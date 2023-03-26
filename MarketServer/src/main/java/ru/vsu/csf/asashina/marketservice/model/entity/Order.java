package ru.vsu.csf.asashina.marketservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "order_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Order {

    @Id
    private String orderNumber;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column(nullable = false)
    private Instant created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order")
    private Set<OrderProduct> products;
}
