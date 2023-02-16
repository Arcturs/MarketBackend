package ru.vsu.csf.asashina.marketserver.model.entity;

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
    private Boolean isPaid = false;

    @Column(nullable = false)
    private Instant created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    @JoinColumn(name = "order_number")
    private Set<OrderProduct> products;
}
