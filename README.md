# Spring Boot Crawler and Extractor

## How to Run
- Clone this repository to your system.
- Make sure you have Java and MySQL server installed.
- If MySQL server is not installed follow this [URL]. Use this while installing ```sudo apt-get install mysql-server-8.0```
- Import  and build in your IDE.
- After that run these commands in terminal to create a databse with name 'product'.
    ```
    Login to your MySQL terminal and replace username and password in application.properties file as per your MySQL Credentials
    create database product;
    use product;
    ```
- Now Run ProjectApplication.java from IDE.
- Then server will be up on port 9191.
## Database
### product_tbl:
- Complete and latest updated information regarding a product like title, description, skuId, latest offer price and latest ratings.
- For a particular skuId we would make entry only once in product_tbl. 
### product_status: 
- Snapshots of product variable details like price and ratings whenever a url is crawled. 
- product_id column is a foreign key referencing to primary key of product table.
-  This was required for getting product details before a particular timestamp.

---
**NOTE**
product_tbl and product_status has one to many bidirectional relationship
---

![GitHub Logo](https://github.com/Real-dev-byte/Crawler-Extractor/blob/8da5efdb0f96e92a0b887dd9d5a2d263b656b009/DatabaseScheme.png
)
## Features
- Fetches Product Details from Amazon URL.(More in Postman API Collection Section)
- Support for Scheduled crawling rate after a particular time delay.
- Producer consumer pattern with Blocking queue for handling scheduling of crawls.
- Response Time of every API in logs.
- Used Thread pool for Crawling.
- Spoofing amazon using randomized user agents.
- Scheduled Crawling in product_status Table snapshot(1 crawl/sec)
![Scheduled Crawling Rate](https://github.com/Real-dev-byte/Crawler-Extractor/blob/2b65c62eea4f6cbd75b0098c86f706691c4c38fd/crawlrate.png)


## [Postman API Collection and Documentation]

## API CONTRACT
&nbsp;
> Returns HTML of given url or skuId and crawls that url.

&nbsp;
#### Request
```
curl --location -g --request GET 'http://localhost:9191/gethtml?url={{amazonUrl}}&skuId={{skuId}}'
```

#### Response
```
{
    "displayMessge": "Successfully fetched HTML from given URL",
    "htmlDocument":"<!doctype html>\n<!--[if IE 8]><html data-19ax5a9jf=\"dingo\" lang=\"en-in\" class=\"a-no-js a-lt-ie10 a-lt-ie9 a-ie8\"><![endif]-->\n<!--[if IE 9]><html data-19ax5a9jf=\"dingo\" lang=\"en-in\" class=\"a-no-j"
}
```
&nbsp;


> Returns Product Details for given url or skuId.If Product is not crawled yet it gets crawled.


#### Request
```
curl --location -g --request GET 'http://localhost:9191/getProductDetails?skuId={{skuId}}&url={{amazonUrl}}'
```

#### Response
```
{
    "displayMessge": "Successfully fetched product details",
    "title": "Himalaya Herbals Purifying Neem Face Wash, 200ml",
    "offerPrice": "₹ 167.00",
    "description": "Specially formulated to give you clear, problem free skin, a soap-free, daily use face wash gel that cleanses your skin by removing excess oil and impurities without over-drying. Neem, well-known for its purifying and antibacterial properties, kills problem-causing bacteria. Combined with Turmeric, it helps control acne and pimples leaving your skin soft, clear, refreshed and problem-free.",
    "ratingsMap": {
        "overallCount": "5,066 ratings",
        "5Star": "60%",
        "4Star": "27%",
        "3Star": "9%",
        "2Star": "2%",
        "1Star": "3%"
    }
}
```

&nbsp;

> Fetches latest product details of a url or skuId before a given timestamp. 


#### Request
```
curl --location -g --request GET 'http://localhost:9191/getProductDetailsBT?timestamp={{timestamp}}&url={{amazonUrl}}&skuId={{skuId}}'
```

#### Response
```
{
    "displayMessge": "Successfully fetched product details",
    "title": "Dr Trust (USA) Waterproof Flexible Tip Digital Thermometer (White) … …",
    "offerPrice": "₹ 299.00",
    "description": "Dr Trust (Usa) Waterproof Flexible Tip Digital Thermometer Comes In White Colour. Flexible Soft Tip Fits Naturally Under Tongue Or Under Arm. Flexible Probe Conforms The Mouth. No Hazard Compared To Broken Mercury Glass Thermometers.",
    "ratingsMap": {
        "overallCount": "5,403 ratings",
        "5Star": "52%",
        "4Star": "29%",
        "3Star": "11%",
        "2Star": "3%",
        "1Star": "6%"
    }
}
```


&nbsp;
> Returns Price Trends for a particular skuId. skuId is mandatory field here.


#### Request
```
curl --location -g --request GET 'http://localhost:9191/getPriceTrend?skuId={{skuId}}'
```

#### Response
```
{
    "displayMessge": "Successfully fetched product details",
    "prices": [
        {
            "timestamp": "2021-05-10 01:12:24.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:12:35.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:12:38.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:12:47.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:12:54.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:13:02.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:13:09.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:13:17.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 01:13:24.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 02:16:07.0",
            "price": "₹ 615.00"
        },
        {
            "timestamp": "2021-05-10 22:52:11.0",
            "price": "₹ 615.00"
        }
    ]
}
```


&nbsp;
> Returns all crawled products till now.


#### Request
```
curl --location --request GET 'http://localhost:9191/getAllProducts'
```

#### Response
```
{
    "displayMessge": "Successfully fetched product details",
    "getProdDetailResponseList": [
        {
            "title": "Pringles Cheddar Cheese, 169G (Pack Of 2)",
            "offerPrice": "₹ 615.00",
            "description": "We Didn'T Use Just Any Cheese Flavor In These Crisps. We Went With Cheddar – The King Of Cheeses. So You Could Even Say It’S A Royal Flavor. It’S So Majestically Good, You Might Not Want To Mention It To Your Lactose-Intolerant Friends.",
            "ratingsMap": {
                "overallCount": "10 ratings",
                "5Star": "16%",
                "4Star": "14%",
                "3Star": "15%",
                "2Star": "27%",
                "1Star": "28%"
            }
        },
        {
            "title": "OnePlus 9R 5G (Carbon Black, 8GB RAM, 128GB Storage) | Extra INR 2,000 OFF on Exchange",
            "offerPrice": "₹ 39,999.00",
            "description": "The All New OnePlus 9 R, Level Up - the New onePlus 9R comes with Qualcomm Snapdragon 870 5G with upgraded Qualcomm Kryo 585 CPU that performs intense mobile computing at up to 3.2 GHz and also comes with an ultra-fast Qualcomm Adreno 650 GPU for superb o",
            "ratingsMap": {
                "overallCount": "1,206 ratings",
                "5Star": "56%",
                "4Star": "24%",
                "3Star": "7%",
                "2Star": "4%",
                "1Star": "9%"
            }
        },
        {
            "title": "The White Willow Orthopaedic Bed Wedge Acid Reflux Memory Foam Pillow for Sleeping ,Back & Neck Pain Relief , Pregnancy Maternity , Anti Snoring- (17.5\" x 18\" x 11\")- Black",
            "offerPrice": "₹ 2,899.00",
            "ratingsMap": {
                "overallCount": "662 ratings",
                "5Star": "49%",
                "4Star": "24%",
                "3Star": "12%",
                "2Star": "6%",
                "1Star": "10%"
            }
        },
        {
            "title": "Dr Trust (USA) Waterproof Flexible Tip Digital Thermometer (White) … …",
            "offerPrice": "₹ 299.00",
            "description": "Dr Trust (Usa) Waterproof Flexible Tip Digital Thermometer Comes In White Colour. Flexible Soft Tip Fits Naturally Under Tongue Or Under Arm. Flexible Probe Conforms The Mouth. No Hazard Compared To Broken Mercury Glass Thermometers.",
            "ratingsMap": {
                "overallCount": "5,403 ratings",
                "5Star": "52%",
                "4Star": "29%",
                "3Star": "11%",
                "2Star": "3%",
                "1Star": "6%"
            }
        },
        {
            "title": "Himalaya Herbals Purifying Neem Face Wash, 200ml",
            "offerPrice": "₹ 167.00",
            "description": "Specially formulated to give you clear, problem free skin, a soap-free, daily use face wash gel that cleanses your skin by removing excess oil and impurities without over-drying. Neem, well-known for its purifying and antibacterial properties, kills probl",
            "ratingsMap": {
                "overallCount": "5,066 ratings",
                "5Star": "60%",
                "4Star": "27%",
                "3Star": "9%",
                "2Star": "2%",
                "1Star": "3%"
            }
        }
    ]
}
```
    
   [Postman API Collection and Documentation]: <https://documenter.getpostman.com/view/14468833/TzRRE96p>
   [URL]: <https://docs.rackspace.com/support/how-to/install-mysql-server-on-the-ubuntu-operating-system/>
## Questions and Comments: rohmetradhananjay@gmail.com   
