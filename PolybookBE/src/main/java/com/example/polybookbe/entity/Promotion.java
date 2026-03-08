package com.example.polybookbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "Promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "chiet_khau")
    private Integer chietKhau;
    @Column(name = "bat_dau")
    private LocalDateTime batDau;
    @Column(name = "ket_thuc")
    private LocalDateTime ketThuc;
    @Column(name = "active")
    private Boolean active = true;
    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Book book;
}
