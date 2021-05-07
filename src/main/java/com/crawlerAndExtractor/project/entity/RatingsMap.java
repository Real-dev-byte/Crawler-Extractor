package com.crawlerAndExtractor.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ratingsMap")
public class RatingsMap {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "5star")
    private String star_5;

    @Column(name = "4star")
    private String star_4;

    @Column(name = "3star")
    private String star_3;

    @Column(name = "2star")
    private String star_2;

    @Column(name = "1star")
    private String star_1;
}
