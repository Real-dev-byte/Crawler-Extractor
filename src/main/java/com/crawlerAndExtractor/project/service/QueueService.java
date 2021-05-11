package com.crawlerAndExtractor.project.service;

import com.crawlerAndExtractor.project.Constants;
import com.crawlerAndExtractor.project.Response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueService {
    private static final Logger log = LoggerFactory.getLogger(QueueService.class);
    private static QueueService instance = null;
    public static BlockingQueue< String > eventQueue = null;


    private ProductService prodService;

    private QueueService() {}

    public static QueueService getInstance() {
        if (instance == null) {
            instance = new QueueService();
        }
        return instance;
    }

    private void initialize() {
        if (eventQueue == null) {
            eventQueue = new LinkedBlockingQueue<String>();
            URLProcessor urlProcessor = new URLProcessor();
            urlProcessor.start();
        }
    }

    public void putEventInQueue(String url, ProductService productService) {
        prodService = productService;
        initialize();
        if(!eventQueue.contains(url)) {
            try {
                eventQueue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class URLProcessor extends Thread {

        @Override
        public void run() {

            for (;;) {
                String url = null;
                try {
                    url = eventQueue.take();
                    String finalUrl = url;
                    BaseResponse baseResponse = null;
                    try {
                        baseResponse = prodService.gethtml(finalUrl, null);
                    } catch (Exception e) {
                        log.error("Error occured while fetching URL: {}",finalUrl);
                        //Push the URL to the end of the queue for retrying crawling this url
                        putEventInQueue(finalUrl,prodService);
                    }
                    finally {
                        log.info("HTML: {}",String.valueOf(baseResponse));
                        Thread.sleep(Constants.URL_CRAWL_DELAY);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
