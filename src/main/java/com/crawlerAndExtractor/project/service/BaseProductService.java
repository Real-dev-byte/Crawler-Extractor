package com.crawlerAndExtractor.project.service;

import com.crawlerAndExtractor.project.Constants;
import com.crawlerAndExtractor.project.helper.RandomUserAgent;
import com.crawlerAndExtractor.project.repository.ProductRepositoryImpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.crawlerAndExtractor.project.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;




public abstract class BaseProductService {
    @Autowired
    private ProductRepositoryImpl productRepository;

    private static final Logger log = LoggerFactory.getLogger(BaseProductService.class);

    public URLtoSKUMapping getDocument(String url, String skuId) throws IOException {
        if(StringUtils.isEmpty(url) && StringUtils.isEmpty(skuId)){
            throw new IllegalArgumentException("Please pass skuId or url as a request parameter.");
        }

        URLtoSKUMapping fetchURL = StringUtils.isEmpty(url)?getURLFromSKU(skuId):getURL(url,skuId);
        Product product = productRepository.fetchProductFromDB(fetchURL.getSkuId());
        log.info(fetchURL.url);
        Document document = null;
        Element Title=null,ProductDescription=null,Price=null;
        try {
//            Connection.Response response = Jsoup.connect(fetchURL.url)
//                    .method(Connection.Method.GET)
//                    .execute();
//            log.info("Cookies: "+response.cookies());
            Map<String, String> cookies = new HashMap<>();
            //String proxyUrl =Constants.PROXY_CRAWL_PREFIX + fetchURL.url; //currently not using proxyurl
            document = Jsoup.connect(fetchURL.url).header("Accept-Encoding", "gzip, deflate")
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .maxBodySize(0)
                    .timeout(0)
                    .cookies(cookies)
                    .get();
            log.info(String.valueOf(document));
                Title = document.getElementById("productTitle");
                ProductDescription = document.select("div#productDescription p").first();
                Price = document.getElementById("priceblock_ourprice");
                if (Price == null) {
                    Price = document.getElementById("priceblock_dealprice");
                }
            String[] Ratings= new String[6];
            Element Star5 =  document.select("table#histogramTable>tbody>tr>td:nth-child(3)>span>a").first();
            Ratings[5] = Objects.nonNull(Star5)?Star5.text():null;
            Element Star4 = document.select("table#histogramTable>tbody>tr:nth-child(2)>td:nth-child(3)>span>a").first();
            Ratings[4] = Objects.nonNull(Star4)?Star4.text():null;
            Element Star3 = document.select("table#histogramTable>tbody>tr:nth-child(3)>td:nth-child(3)>span>a").first();
            Ratings[3] = Objects.nonNull(Star3)?Star3.text():null;
            Element Star2 = document.select("table#histogramTable>tbody>tr:nth-child(4)>td:nth-child(3)>span>a").first();
            Ratings[2] = Objects.nonNull(Star2)?Star2.text():null;
            Element Star1 = document.select("table#histogramTable>tbody>tr:nth-child(5)>td:nth-child(3)>span>a").first();
            Ratings[1] = Objects.nonNull(Star1)?Star1.text():null;
            Element OverallCount = document.select("div#averageCustomerReviews>span:nth-child(3)>a>span").first();
            Boolean retry = retryCrawlingIfFailed(Title,Price,ProductDescription,OverallCount,Ratings);
            if(retry){
                throw new IOException("Unable to crawl. Try later");
            }
            productRepository.createProduct(fetchURL,product,fetchURL.skuId,Title,Price,ProductDescription,OverallCount,Ratings);
        }
        catch (IOException e){
            throw new IOException(String.format("Could not fetch HTML from given url: %s. Please enter correct URL",fetchURL.url));
        }
        fetchURL.setDocument(document);
        return fetchURL;
    }

    private Boolean retryCrawlingIfFailed(Element title, Element price, Element productDescription, Element overallCount, String[] ratings) {
        if(Objects.isNull(title) && Objects.isNull(price) && Objects.isNull(productDescription) && Objects.isNull(overallCount)){
            for (String rating:ratings){
                if(StringUtils.isNotEmpty(rating)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected URLtoSKUMapping getURL(String url, String skuId) {
        URLtoSKUMapping urlSkuMap = new URLtoSKUMapping();
        String SKUID = "";
        SKUID = populateSkuIdFromURL(SKUID,url,"/dp/");
        url = "https://www.amazon.in/dp/" + SKUID;
        urlSkuMap.setSkuId(SKUID);
        urlSkuMap.setUrl(url);
        return urlSkuMap;
    }
    protected URLtoSKUMapping getURLFromSKU(String skuId) {
        URLtoSKUMapping urlSkuMap = new URLtoSKUMapping();
        String url = "https://www.amazon.in/dp/" + skuId;
        urlSkuMap.setSkuId(skuId);
        urlSkuMap.setUrl(url);
        return urlSkuMap;
    }

    private String populateSkuIdFromURL(String SKUID,String url, String s) {

        int idx = url.indexOf(s);
        if(idx == -1)
            return SKUID;


        for(int i=idx + s.length();i<url.length();i++){
            char c = url.charAt(i);
            if(c == '/' || c == '?')
                break;
            SKUID += c;
        }
        return SKUID;
    }




    @NoArgsConstructor
    @Data
    public class URLtoSKUMapping{
        private String url;
        private String skuId;
        private Document document;
        private Product product;
    }
}
