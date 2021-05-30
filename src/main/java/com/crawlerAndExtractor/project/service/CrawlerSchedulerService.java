package com.crawlerAndExtractor.project.service;

import com.crawlerAndExtractor.project.Constants;
import com.crawlerAndExtractor.project.entity.Product;
import com.crawlerAndExtractor.project.repository.ProductRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

@Component
public class CrawlerSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(CrawlerSchedulerService.class);

    private final ProductRepositoryImpl productRepository;

    private final ProductService productService;

    private final TaskExecutor taskExecutor;

    public CrawlerSchedulerService(ProductRepositoryImpl productRepository, ProductService productService, TaskExecutor taskExecutor) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedRate = Constants.CRAWL_TIME_DIFFERENCE)
    public void crawlPages(){

        List<Product> productList = productRepository.findAllProducts();
        for(Product product:productList){
            Date date = new Date();
            long currentTimestamp = date.getTime();
            Timestamp lastUpdated = product.getUpdated_at();
            long diff = currentTimestamp - lastUpdated.getTime();
            String ProductUrl = "https://www.amazon.in/dp/" + product.getSkuId();
            //log.info("For URL: {} time lastUpdated is {} ",ProductUrl, diff);
            if(diff >= Constants.CRAWL_TIME_DIFFERENCE){
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO add long running task
                        QueueService.getInstance().putEventInQueue(ProductUrl,productService);
                    }
                });

            }
        }
    }

}
