package com.crawlerAndExtractor.project.repository;

import com.crawlerAndExtractor.project.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusRepository extends JpaRepository<ProductStatus,Integer> {
}
