package com.crawlerAndExtractor.project.service;
import com.crawlerAndExtractor.project.Response.BaseResponse;
import com.crawlerAndExtractor.project.Response.GetProdDetailResponse;
import com.crawlerAndExtractor.project.entity.Product;
import com.crawlerAndExtractor.project.entity.ProductStatus;
import com.crawlerAndExtractor.project.repository.ProductRepositoryImpl;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ProductService extends BaseProductService{
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepositoryImpl productRepository;

    public ProductService(ProductRepositoryImpl productRepository) {
        super(productRepository);
        this.productRepository = productRepository;
    }

    /***
     *
     * @param url
     * @param skuId
     * @returnBaseResponse
     * Crawls given URL and returns its HTML
     * @throws IOException
     */
    public BaseResponse gethtml(String url, String skuId) throws IOException {
        BaseResponse response = new BaseResponse();
            URLtoSKUMapping urLtoSKUMapping = getDocument(url,skuId);
            String docHTML = null;

            if(Objects.nonNull(urLtoSKUMapping)){
                Document document = urLtoSKUMapping.getDocument();
                if(Objects.nonNull(document)) {
                    docHTML = document.toString();
                }
            }
            response.setHtmlDocument(docHTML);
            return response;
    }

    /***
     *
     * @param url
     * @param skuId
     * @return GetProdDetailResponse
     * Fetches latest Product Details.
     * @throws Exception
     */
    public GetProdDetailResponse getProductDetails(String url, String skuId) throws Exception {
        GetProdDetailResponse response = new GetProdDetailResponse();
        URLtoSKUMapping urLtoSKUMapping = StringUtils.isEmpty(url)?getURLFromSKU(skuId):getURL(url,skuId);
        Product product = productRepository.fetchProductFromDB(urLtoSKUMapping.getSkuId());

        if(Objects.isNull(product)){
            log.info("Product with skuId: {} not found in db. Fetching from given url...",urLtoSKUMapping.getSkuId());
            URLtoSKUMapping urlToSkuWithProd=getDocument(url,skuId);
            product=urlToSkuWithProd.getProduct();
        }
        responsePopulator(product.getTitle(),product.getLatestOfferPrice(), product.getDescription(),
                product.getLatestOverAllCount(), product.getStar1(), product.getStar2(), product.getStar3(),
                product.getStar4(), product.getStar5(), response);

        return response;
    }

    /***
     *
     * @param url
     * @param skuId
     * @param timestamp
     * @return GetProdDetailResponse
     * Fetches latest Product Details before given Timestamp.
     * @throws Exception
     */
    public GetProdDetailResponse getProductDetailsBT(String url, String skuId, Timestamp timestamp) throws Exception{
        GetProdDetailResponse response = new GetProdDetailResponse();
        URLtoSKUMapping urLtoSKUMapping = StringUtils.isEmpty(url)?getURLFromSKU(skuId):getURL(url,skuId);

        Product productFromDB = productRepository.fetchProductFromDB(urLtoSKUMapping.getSkuId());

        //Product was never crawled
        if(Objects.isNull(productFromDB)){
            String message = String.format("Either Product with skuId: %s not crawled or Wrong URL entered",urLtoSKUMapping.getSkuId());
            log.error(message);
            throw new Exception(message);
        }

        //Product was created after the given timestamp
        ProductStatus productStatus = productRepository.fetchProductStatusBeforeDate(productFromDB,timestamp);
        if(Objects.isNull(productStatus)){
            String message = String.format("Product with skuId: %s Absent in DB before time: %s",skuId,timestamp);
            log.error(message);
            throw new Exception(message);
        }
        log.info("Product Status TimeStamp: {}", productStatus.getCreatedAt());
        responsePopulator(productFromDB.getTitle(),productStatus.getOfferPrice(),productFromDB.getDescription()
                ,productStatus.getOverallCount(),productStatus.getStar1(),productStatus.getStar2(),productStatus.getStar3()
                ,productStatus.getStar4(),productStatus.getStar5(),response);
        return response;
    }

    /***
     *
     * @param skuId
     * @return GetProdDetailResponse
     *Fetches price trend for given skuId.
     * @throws Exception
     */
    public GetProdDetailResponse getAllPriceForProduct(String skuId) throws Exception {
        GetProdDetailResponse prodDetailResponse = new GetProdDetailResponse();
        log.info("Passed skuId: {}",skuId);
        List<ProductStatus> productStatusList = productRepository.findAllProductStatusForSkuId(skuId);
        if(CollectionUtils.isEmpty(productStatusList)){
            String message = String.format("SkuId: %s not crawled",skuId);
            log.error(message);
            throw new Exception(message);
        }

        List<GetProdDetailResponse.PriceTrend> priceTrendList = new ArrayList<>();
        for(ProductStatus productStatus:productStatusList){
            GetProdDetailResponse.PriceTrend priceTrend = new GetProdDetailResponse.PriceTrend();
            priceTrend.setPrice(productStatus.getOfferPrice());
            priceTrend.setTimestamp(productStatus.getCreatedAt().toString());
            priceTrendList.add(priceTrend);
            log.info("Created at :{}",productStatus.getCreatedAt());
        }
        prodDetailResponse.setPrices(priceTrendList);
        return prodDetailResponse;
    }

    /***
     *
     * @return BaseResponse
     * Handles /getAllProducts. Fetches all products from DB.
     */
    public BaseResponse getAllProducts() {
        BaseResponse response = new BaseResponse();
        List<Product> productList= productRepository.findAllProducts();
        List<GetProdDetailResponse> getProdDetailResponseList = new ArrayList<>();
        for(Product product:productList){
            GetProdDetailResponse getProdDetailResponse = new GetProdDetailResponse();
            getProdDetailResponse.setOfferPrice(product.getLatestOfferPrice());
            getProdDetailResponse.setTitle(product.getTitle());
            getProdDetailResponse.setDescription(product.getDescription());
            log.info("Product skuId: {}",product.getSkuId());
            GetProdDetailResponse.ratingsMap ratingsMap = new GetProdDetailResponse.ratingsMap();
            ratingsMap.setOverallCount(product.getLatestOverAllCount());
            ratingsMap.setStar_1(product.getStar1());
            ratingsMap.setStar_2(product.getStar2());
            ratingsMap.setStar_3(product.getStar3());
            ratingsMap.setStar_4(product.getStar4());
            ratingsMap.setStar_5(product.getStar5());
            getProdDetailResponse.setRatingsMap(ratingsMap);

            getProdDetailResponseList.add(getProdDetailResponse);
        }
        response.setGetProdDetailResponseList(getProdDetailResponseList);
        return response;
    }

    private void responsePopulator(String title, String offerPrice, String description, String overallCount, String star1, String star2, String star3, String star4, String star5, GetProdDetailResponse response) {
        GetProdDetailResponse.ratingsMap ratingsMap = new GetProdDetailResponse.ratingsMap();
        response.setTitle(title);
        response.setDescription(description);
        response.setOfferPrice(offerPrice);
        ratingsMap.setOverallCount(overallCount);
        ratingsMap.setStar_1(star1);
        ratingsMap.setStar_2(star2);
        ratingsMap.setStar_3(star3);
        ratingsMap.setStar_4(star4);
        ratingsMap.setStar_5(star5);
        response.setRatingsMap(ratingsMap);
    }
}
