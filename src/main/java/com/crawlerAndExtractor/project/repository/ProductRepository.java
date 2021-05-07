package com.crawlerAndExtractor.project.repository;

import com.crawlerAndExtractor.project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product,Integer>{

     @Query(value = "SELECT P FROM Product  P WHERE P.skuId = ?1")
     Product findProductBySkuId(String skuId);
}