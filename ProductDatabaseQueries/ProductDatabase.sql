/*
a. product: the “product” tables should include the name, category of the product;
it should also provide information to tell us when this product was added to the table,
and who added this product.
*/

CREATE TABLE products(

	product_id INT NOT NULL PRIMARY KEY,
	name VARCHAR(255) NOT NULL ,
	category VARCHAR(255) NOT NULL,
	created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	created_by VARCHAR(255) NOT NULL

);

/*
b. product price: this table will store the current price information of each product
and we should be able to join it with the product table.
It should include the price current discount percent(default to 0), the updated time and who updated it.
*/

CREATE TABLE product_prices(

	product_id INT NOT NULL PRIMARY KEY,
	price DECIMAL(10,2) NOT NULL,
	discount_percentage DECIMAL(5,2) DEFAULT 0,
	updated_time TIMESTAMP NOT NULL,
	updated_by VARCHAR(255) NOT NULL,
	FOREIGN KEY (product_id) REFERENCES products(product_id)

);

/*
c. product price change log: this table will store the old and new value of the
“product price” table that’s impacted by any insert/update/delete and contains
information of who and when the operation was performed.
*/

CREATE TABLE price_change_logs(

	old_price DECIMAL(10,2) NOT NULL,
	new_price DECIMAL (10,2) NOT NULL ,
	impacted_by VARCHAR(255) NOT NULL,
	impacted_at TIMESTAMP NOT NULL
);

/*
Query to join “product” table and “product price” table together to show the product name,
category, price, and who/when it gets updated.
*/

SELECT
	products.name,
	products.category,
	product_prices.price,
	product_prices.updated_time,
	product_prices.updated_by
FROM products
INNER JOIN product_prices ON products.product_id = product_prices.product_id;