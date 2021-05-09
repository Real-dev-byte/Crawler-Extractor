# Spring Boot Crawler and Extractor

## How to Run
- Clone this repository to your system.
- Make sure you have Java and MySQL server installed.
- If MySQL server is not installed follow this [URL]. Use this while installing ```sudo apt-get install mysql-server-8.0```
- Import  and build in your IDE.
- After that run these commands in terminal to create a databse with name 'product'.
    ```
    Login to your MySQL terminal and replace username and password in application.properties file as per your MySQL Credentials
    create scheme product
    use product
    ```
- Now Run ProjectApplication.java from IDE.
- Then server will be up on port 9191.
## Database
### product_tbl:
- Complete and latest updated information regarding a product like title, description, skuId, latest offer price and latest ratings.
- For a particular skuId we would make entry only once in product_tbl. 
### product_status: 
- Snapshots of product variable details like price and ratings. product_id column is a foreign key referencing to primary key of product table.
-  This was required for getting product details before a particular timestamp.

_Note: producttbl and product_status has one to many bidirectional relationship_

![GitHub Logo](https://github.com/Real-dev-byte/Crawler-Extractor/blob/8da5efdb0f96e92a0b887dd9d5a2d263b656b009/DatabaseScheme.png
)
## Features
- Fetches Product Details from Amazon URL.(More in Postman API Collection Section)
- Support for Scheduled crawling rate after a particular time delay.
- Producer consumer pattern with shared queue for handling scheduling of crawls.
- Auto replay upon failed crawling of a particular URL while doing scheduled crawling.
- Response Time of every API in logs.
## [Postman API Collection and Documentation]
    
   [Postman API Collection and Documentation]: <https://documenter.getpostman.com/view/14468833/TzRRE96p>
   [URL]: <https://docs.rackspace.com/support/how-to/install-mysql-server-on-the-ubuntu-operating-system/>
## Questions and Comments: rohmetradhananjay@gmail.com   
