package com.crawlerAndExtractor.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_status")
public class ProductStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String offerPrice;

    private String overallCount;

    private String Star5;
    private String Star4;
    private String Star3;
    private String Star2;
    private String Star1;

    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @JsonBackReference
    public Product getProduct() {
        return product;
    }
}
