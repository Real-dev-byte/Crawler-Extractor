# Spring Boot Crawler and Extractor

## How to Run
- Clone this repository to your system.
- Make sure you have Java and MySQL installed.
- Import  and build in your IDE.
- After that run these commands in terminal to create a databse with name 'product'.
    ```
    sudo mysql -u root
    create scheme product
    use product
    ```
- Now Run ProjectApplication.java 
- Then server will be up on port 9191.
## Database
### Product:
Complete and latest updated information regarding a product like title, description, skuId, etc.
### Product Status: 
Snapshots of product variable details like price and ratings.

![GitHub Logo](https://github.com/Real-dev-byte/Crawler-Extractor/blob/8da5efdb0f96e92a0b887dd9d5a2d263b656b009/DatabaseScheme.png
)
## Features
- Fetches Product Details from Amazon URL.(More in Postman API Collection Section)
- Hourly crawling of all product urls in database.
- Producer consumer pattern with shared queue for handling hourly crawls.
- Response Time of every API in logs.
## [Postman API Collection and Documentation]

   [Postman API Collection and Documentation]: <https://documenter.getpostman.com/view/14468833/TzRRE96p>
## Questions and Comments: rohmetradhananajay@gmail.com   
