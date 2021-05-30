package com.crawlerAndExtractor.project.repository;

import com.crawlerAndExtractor.project.entity.Product;
import com.crawlerAndExtractor.project.entity.ProductStatus;
import com.crawlerAndExtractor.project.service.BaseProductService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;

@Component
public class ProductRepositoryImpl {
    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    private final ProductRepository productRepository;

    public ProductRepositoryImpl(@Lazy ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(BaseProductService.URLtoSKUMapping fetcURL, Product product, String skuId, Element Title, Element Price, Element ProductDescription, Element OverallCount,String[] Ratings) {

        String title = Objects.nonNull(Title)?Title.text():null;
        String price = Objects.nonNull(Price)?Price.text():null;
        String productDescription = Objects.nonNull(ProductDescription)?ProductDescription.text():null;
        String overallCount = Objects.nonNull(OverallCount)?OverallCount.text():null;

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        if(Objects.isNull(product)) {
            product = new Product();
            product.setSkuId(skuId);
            product.setCreated_at(timestamp);
        }
        if (StringUtils.isNotEmpty(productDescription))
            product.setDescription(productDescription); // description can change
        if(StringUtils.isNotEmpty(title))
            product.setTitle(title); //Title can change
        if(StringUtils.isNotEmpty(price))
            product.setLatestOfferPrice(price); //LatestOfferPrice can change
        if(StringUtils.isNotEmpty(overallCount))
            product.setLatestOverAllCount(overallCount);    //LatestOverAllCount can change

        product.setUpdated_at(timestamp);

        ProductStatus productStatus=new ProductStatus();
        productStatus.setOfferPrice(price);
        productStatus.setOverallCount(overallCount);
        productStatus.setCreatedAt(timestamp);
        productStatus.setProduct(product);

        //Set Ratings Percentage for product
        product.setStar1(Ratings[1]);
        product.setStar2(Ratings[2]);
        product.setStar3(Ratings[3]);
        product.setStar4(Ratings[4]);
        product.setStar5(Ratings[5]);

        //Set Ratings Percentage for product_status because
        // if we want to fetch product details before a timestamp
        productStatus.setStar1(Ratings[1]);
        productStatus.setStar2(Ratings[2]);
        productStatus.setStar3(Ratings[3]);
        productStatus.setStar4(Ratings[4]);
        productStatus.setStar5(Ratings[5]);

        //add productStatus to ProductStatusList
        List<ProductStatus> productStatusList = product.getProductStatuses();
        if(CollectionUtils.isEmpty(productStatusList)){
            productStatusList = new ArrayList<>();
        }
        productStatusList.add(productStatus);


        product.setProductStatuses(productStatusList);
        productRepository.save(product);
        fetcURL.setProduct(product);
        return product;
    }

    public Product fetchProductFromDB(String skuId) {
        log.info("Finding Product with skuId:{}in db",skuId);
        Product product=productRepository.findProductBySkuId(skuId);
        return product;
    }

    public ProductStatus fetchProductStatusBeforeDate(Product product, Timestamp date){
        ProductStatus productStatus1 = null;

        List<ProductStatus> productStatuses = product.getProductStatuses();
        Collections.reverse(productStatuses);
        log.info("Fetching product just before: {}",date);
        for (ProductStatus productStatus:productStatuses){
            int b = date.compareTo(productStatus.getCreatedAt());
            if(b >= 0){
                productStatus1 = productStatus;
                break;
            }
        }
        return productStatus1;
    }


    public List<Product> findAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList;
    }

    public List<ProductStatus> findAllProductStatusForSkuId(String skuId){
        List<ProductStatus> productStatusList = productRepository.findAllProductStatus (skuId);
        return productStatusList;
    }


}
