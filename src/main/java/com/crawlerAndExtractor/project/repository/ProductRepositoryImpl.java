package com.crawlerAndExtractor.project.repository;

import com.crawlerAndExtractor.project.entity.Product;
import com.crawlerAndExtractor.project.entity.ProductStatus;
import com.crawlerAndExtractor.project.service.BaseProductService;
import com.crawlerAndExtractor.project.service.ProductService;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Component
public class ProductRepositoryImpl {
    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(BaseProductService.URLtoSKUMapping fetcURL, Product product, String skuId, Element Title, Element Price, Element ProductDescription, Element OverallCount) {

        String title = Title != null?Title.text():null;
        String price = Price != null?Price.text():null;
        String productDescription = ProductDescription!=null?ProductDescription.text():null;
        String overallCount = OverallCount!=null?OverallCount.text():null;

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        if(Objects.isNull(product)) {
            product = new Product();
            product.setDescription(productDescription);
            product.setTitle(title);
            product.setSkuId(skuId);
            product.setCreated_at(timestamp);
        }

        ProductStatus productStatus=new ProductStatus();
        productStatus.setOfferPrice(price);
        productStatus.setOverallCount(overallCount);
        productStatus.setCreatedAt(timestamp);
        productStatus.setProduct(product);
        List<ProductStatus> productStatusList = product.getProductStatuses();
        if(CollectionUtils.isEmpty(productStatusList)){
            productStatusList = new ArrayList<>();
        }
        productStatusList.add(productStatus);
        product.setLatestOverAllCount(overallCount);
        product.setLatestOfferPrice(price);
        product.setUpdated_at(timestamp);
        product.setProductStatuses(productStatusList);
        productRepository.save(product);
        fetcURL.setProduct(product);
        return product;
    }

    public Product fetchProductFromDB(String skuId) {
        Product product=productRepository.findProductBySkuId(skuId);
        if(Objects.nonNull(product)){
            log.info("Product found in db "+product.getSkuId());
        }
        return product;
    }

    public ProductStatus fetchProductStatusBeforeDate(Product product,String skuId, Timestamp date){
        ProductStatus productStatus1 = null;
        List<ProductStatus> productStatuses = getProductStatusListSortedByTime(product);

        for (ProductStatus productStatus:productStatuses){
            int b = date.compareTo(productStatus.getCreatedAt());
            if(b >= 0){
                productStatus1 = productStatus;
                break;
            }
        }
        return productStatus1;
    }

    public List<ProductStatus> getProductStatusListSortedByTime(Product product) {
        List<ProductStatus> productStatuses = product.getProductStatuses();
        Collections.sort(productStatuses, new Comparator<ProductStatus>() {
            @Override
            public int compare(ProductStatus o1, ProductStatus o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });
        return productStatuses;
    }

    public List<Product> findAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList;
    }
}
