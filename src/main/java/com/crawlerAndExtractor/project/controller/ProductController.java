package com.crawlerAndExtractor.project.controller;

import com.crawlerAndExtractor.project.Response.BaseResponse;
import com.crawlerAndExtractor.project.service.CrawlerSchedulerService;
import com.crawlerAndExtractor.project.service.ProductService;
import io.swagger.annotations.ApiParam;
import org.mapstruct.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;


@RestController
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/gethtml", method = RequestMethod.GET)
    public BaseResponse gethtml(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpServletResponse,
                                @ApiParam @RequestParam(name = "url") String url,
                                @ApiParam  @RequestParam(required = false,name = "skuId") String skuId) {
        try {
            BaseResponse response;
            response = productService.gethtml(url,skuId);
            response.setStatusCode(200);
            response.setDisplayMessge("Successfully fetched HTML from given URL");
            return response;
        }
        catch (Exception e){
            BaseResponse response = new BaseResponse();
            response.setDisplayMessge(e.getMessage());
            response.setStatusCode(400);
            return response;
        }

    }

    @RequestMapping(value = "/getProductDetails", method = RequestMethod.GET)
    public BaseResponse getProductDetails(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpServletResponse,
                                          @ApiParam @RequestParam(name = "url") String url,
                                          @ApiParam  @RequestParam(required = false,name = "skuId") String skuId) {
        try {
            BaseResponse response;
            response = productService.getProductDetails(url,skuId);
            response.setStatusCode(200);
            response.setDisplayMessge("Successfully fetched product details");
            return response;
        }
        catch (Exception e){
            BaseResponse response = new BaseResponse();
            response.setDisplayMessge(e.getMessage());
            response.setStatusCode(400);
            return response;
        }
    }

    @RequestMapping(value = "/getProductDetailsBT", method = RequestMethod.GET)
    public BaseResponse getProductDetailsBeforeTimestamp(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpServletResponse,
                                          @ApiParam @RequestParam(name = "url") String url,
                                          @ApiParam  @RequestParam(required = false,name = "skuId") String skuId,
                                                         @ApiParam @RequestParam(name = "timestamp")Timestamp timestamp) {
        try {
            BaseResponse response;
            httpRequest.setAttribute("date",new Date());
            response = productService.getProductDetails(url,skuId,timestamp);
            response.setStatusCode(200);
            response.setDisplayMessge("Successfully fetched product details");
            return response;
        }
        catch (Exception e){
            BaseResponse response = new BaseResponse();
            response.setDisplayMessge(e.getMessage());
            response.setStatusCode(400);
            return response;
        }
    }

    @RequestMapping(value = "/getPriceTrend", method = RequestMethod.GET)
    public BaseResponse getPriceTrend(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpServletResponse,
                                          @ApiParam @RequestParam(name = "url") String url,
                                          @ApiParam  @RequestParam(required = false,name = "skuId") String skuId) {
        try {
            BaseResponse response;
            response = productService.getAllPriceForProduct(url,skuId);
            response.setStatusCode(200);
            response.setDisplayMessge("Successfully fetched product details");
            return response;
        }
        catch (Exception e){
            BaseResponse response = new BaseResponse();
            response.setDisplayMessge(e.getMessage());
            response.setStatusCode(400);
            return response;
        }
    }

    @RequestMapping(value = "/getAllProducts", method = RequestMethod.GET)
    public BaseResponse getAllProducts(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpServletResponse) {
        try {
            BaseResponse response;
            response = productService.getAllProducts();
            response.setStatusCode(200);
            response.setDisplayMessge("Successfully fetched product details");
            return response;
        }
        catch (Exception e){
            BaseResponse response = new BaseResponse();
            response.setDisplayMessge(e.getMessage());
            response.setStatusCode(400);
            return response;
        }
    }
}
