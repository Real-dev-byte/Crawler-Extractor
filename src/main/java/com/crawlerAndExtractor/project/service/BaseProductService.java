package com.crawlerAndExtractor.project.service;

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


public class BaseProductService {
    @Autowired
    private ProductRepositoryImpl productRepository;

    private static final Logger log = LoggerFactory.getLogger(BaseProductService.class);

    public URLtoSKUMapping getDocument(String url, String skuId) throws IOException {
        if(StringUtils.isEmpty(url) && StringUtils.isEmpty(skuId)){
            throw new IllegalArgumentException("Please pass skuId or url as a request parameter.");
        }

        URLtoSKUMapping fetchURL = getURL(url,skuId);
        Product product = productRepository.fetchProductFromDB(fetchURL.getSkuId());
        log.info(fetchURL.url);
        Document document = null;
        Product fetchedProduct=null;
        Element Title=null,ProductDescription=null,Price=null;
        try {
            /*Connection.Response response = Jsoup.connect(fetchURL.url)
                    .method(Connection.Method.GET)
                    .execute();
            log.info("Cookies: "+response.cookies());
            Map<String, String> cookies = response.cookies();*/

            document = Jsoup.connect("https://api.proxycrawl.com?token=iQx_YpkQ84k0B6TSo0sLkA&url="+fetchURL.url).header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .timeout(600000)
                    //.cookies(cookies)
                    .get();

            if(Objects.isNull(product)) {
                Title = document.getElementById("productTitle");
                ProductDescription = document.select("div#productDescription p").first();
            }
                Price = document.getElementById("priceblock_ourprice");
                if (Price == null) {
                    Price = document.getElementById("priceblock_dealprice");
                }

            Element OverallCount = document.select("div#averageCustomerReviews>span:nth-child(3)>a>span").first();
            fetchedProduct = productRepository.createProduct(fetchURL,product,fetchURL.skuId,Title,Price,ProductDescription,OverallCount);
        }
        catch (IOException e){
            throw new IOException(String.format("Could not fetch HTML from given url: {}",fetchURL.url));
        }
        fetchURL.setDocument(document);
        return fetchURL;
    }

    protected URLtoSKUMapping getURL(String url, String skuId) {
        URLtoSKUMapping urlSkuMap = new URLtoSKUMapping();
        String SKUID = "";
        SKUID = populateSkuIdFromURL(SKUID,url,"/dp/");
        if(StringUtils.isEmpty(SKUID)){
            log.info("gp type product");
            SKUID = populateSkuIdFromURL(SKUID,url,"/gp/product/");
        }
        url = "https://www.amazon.in" + SKUID;
        urlSkuMap.setSkuId(SKUID);
        urlSkuMap.setUrl(url);
        return urlSkuMap;
    }

    private String populateSkuIdFromURL(String SKUID,String url, String s) {

        int idx = url.indexOf(s);
        if(idx == -1)
            return SKUID;

        SKUID += s;
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
