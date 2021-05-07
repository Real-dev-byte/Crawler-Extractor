package com.crawlerAndExtractor.project.entity;
import java.sql.Timestamp;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_TBL")
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    private String title;

    private String description;

    private String skuId;

    private String latestOfferPrice;

    private String latestOverAllCount;

    private Timestamp created_at;

    private Timestamp updated_at;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "product")
    private List<ProductStatus> productStatuses = new ArrayList<>();

    @JsonManagedReference
    public List<ProductStatus> getProductStatuses() {
        return productStatuses;
    }
}