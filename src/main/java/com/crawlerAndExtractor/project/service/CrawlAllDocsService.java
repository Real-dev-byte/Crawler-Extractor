package com.crawlerAndExtractor.project.service;

import com.crawlerAndExtractor.project.Response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlAllDocsService {
    private static final Logger log = LoggerFactory.getLogger(CrawlAllDocsService.class);

    @Autowired
    private ProductService productService;
    public void crawlPageGatewayService(String ProductUrl) {
        try {
            BaseResponse baseResponse = productService.gethtml(ProductUrl,null);
            log.info("HTML is " + baseResponse.getHtmlDocument());
        }
        catch (Exception e){
            log.info(e.getMessage());
        }

    }
}
