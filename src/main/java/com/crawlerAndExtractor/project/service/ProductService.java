package com.crawlerAndExtractor.project.service;
import com.crawlerAndExtractor.project.Response.BaseResponse;
import com.crawlerAndExtractor.project.Response.GetProdDetailResponse;
import com.crawlerAndExtractor.project.entity.Product;
import com.crawlerAndExtractor.project.entity.ProductStatus;
import com.crawlerAndExtractor.project.repository.ProductRepositoryImpl;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ProductService extends BaseProductService{
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private ProductRepositoryImpl productRepository;
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
     * @return Handles response for /getProductDetails. Fetches latest Product Details.
     * @throws Exception
     */
    public GetProdDetailResponse getProductDetails(String url, String skuId) throws Exception {
        Date date = new Date();
        return getProductDetailsHelper(url,skuId,new Timestamp(date.getTime()),true);
    }

    /***
     *
     * @param url
     * @param skuId
     * @param timestamp
     * @return Handles response for /getProductDetailsBT. Fetches latest Product Details before given Timestamp.
     * @throws Exception
     */
    public GetProdDetailResponse getProductDetails(String url, String skuId, Timestamp timestamp) throws Exception{
        return getProductDetailsHelper(url,skuId,timestamp,false);
    }

    /***
     *
     * @param url
     * @param skuId
     * @param timestamp
     * @param getLatestOfferPrice
     * @return
     * Flag getLatestOfferPrice decides if this call is regarding /getProductDetails or /getProductDetailsBT.
     * If the flag is set(/getProductDetails) we can directly fetch the response from the product as it has latestOverAllCount and latestOfferPrice.
     * Else we find product status before that timestamp and grab the required fields from respective Product Status.
     * @throws Exception
     */
    private GetProdDetailResponse getProductDetailsHelper(String url, String skuId, Timestamp timestamp,Boolean getLatestOfferPrice) throws Exception {
        GetProdDetailResponse response = new GetProdDetailResponse();
        URLtoSKUMapping urLtoSKUMapping = getURL(url,skuId);
        Product product = productRepository.fetchProductFromDB(urLtoSKUMapping.getSkuId());
        ProductStatus productStatus = null;

        if(Objects.isNull(product) && getLatestOfferPrice){
            log.info("Product with skuId: {} not found in db. Fetching from given url...",urLtoSKUMapping.getSkuId());
            URLtoSKUMapping urlToSkuWithProd=getDocument(url,skuId);
            product=urlToSkuWithProd.getProduct();
        }
        else if(Objects.nonNull(product) && !getLatestOfferPrice){
            log.info("Product with skuId: {} present in db. Fetching overallcount and offer Price from DB before timestamp: {} ...",product.getSkuId(),timestamp);
            productStatus = productRepository.fetchProductStatusBeforeDate(product,urLtoSKUMapping.getSkuId(),timestamp);
        }

        if(getLatestOfferPrice){
            if(Objects.isNull(product)){
                String message = "Could not fetch product from given url or skuId";
                log.info(message);
                throw new Exception(message);
            }
        }
        else {
            if(Objects.isNull(product) || Objects.isNull(productStatus)){
                String message = "Product Absent in DB before time " + timestamp;
                log.info(message);
                throw new Exception(message);
            }
        }

        if(!getLatestOfferPrice && Objects.nonNull(productStatus)){
            log.info("Product in DB before timestamp: {} was created at: {}",timestamp,productStatus.getCreatedAt());
        }
        GetProdDetailResponse.ratingsMap ratingsMap = new GetProdDetailResponse.ratingsMap();

        response.setTitle(product.getTitle());
        response.setDescription(product.getDescription());
        if(getLatestOfferPrice){
            response.setOfferPrice(product.getLatestOfferPrice());
            ratingsMap.setOverallCount(product.getLatestOverAllCount());
        }
        else {
            response.setOfferPrice(productStatus.getOfferPrice());
            ratingsMap.setOverallCount(productStatus.getOverallCount());
        }
        response.setRatingsMap(ratingsMap);
        return response;
    }

    /***
     *
     * @param url
     * @param skuId
     * @return Handles /getPriceTrend.Fetches price trend for given skuId or url.
     * @throws Exception
     */
    public GetProdDetailResponse getAllPriceForProduct(String url, String skuId) throws Exception {
        GetProdDetailResponse prodDetailResponse = new GetProdDetailResponse();
        URLtoSKUMapping urLtoSKUMapping = getURL(url,skuId);
        Product product = productRepository.fetchProductFromDB(urLtoSKUMapping.getSkuId());
        if(Objects.isNull(product)){
            String message = String.format("Link: {} not crawled",urLtoSKUMapping.getUrl());
            log.info(message);
            throw new Exception(message);
        }
        List<ProductStatus> productStatusList = productRepository.getProductStatusListSortedByTime(product);
        List<GetProdDetailResponse.prices> pricesList = new ArrayList<>();
        for(ProductStatus productStatus:productStatusList){
            GetProdDetailResponse.prices prices1 = new GetProdDetailResponse.prices();
            prices1.setPrice(productStatus.getOfferPrice());
            prices1.setTimestamp(productStatus.getCreatedAt());
            pricesList.add(prices1);
        }
        prodDetailResponse.setPrices(pricesList);
        return prodDetailResponse;
    }

    /***
     *
     * @return Handles /getAllProducts. Fetches all products from DB.
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
            getProdDetailResponse.setRatingsMap(ratingsMap);

            getProdDetailResponseList.add(getProdDetailResponse);
        }
        response.setGetProdDetailResponseList(getProdDetailResponseList);
        return response;
    }
}
