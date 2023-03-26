package ru.vsu.csf.asashina.marketservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Table(name = "refresh_token")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {

    @Id
    private String refreshToken;

    @Column(nullable = false)
    private Instant expireDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
