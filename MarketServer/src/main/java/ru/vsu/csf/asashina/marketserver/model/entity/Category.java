package ru.vsu.csf.asashina.marketserver.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "category")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(unique = true, nullable = false)
    private String name;
}
