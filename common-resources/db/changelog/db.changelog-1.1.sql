--liquibase formatted sql

--changeSet Anton D.:1
CREATE TABLE product_monthly_limit
(
    id SERIAL PRIMARY KEY ,
    change_date_time TIMESTAMP,
    current_limit NUMERIC(18,2),
    limit_left NUMERIC(18,2)
);

CREATE TABLE service_monthly_limit
(
    id SERIAL PRIMARY KEY ,
    change_date_time TIMESTAMP,
    current_limit NUMERIC(18,2),
    limit_left NUMERIC(18,2)
);

INSERT INTO product_monthly_limit (change_date_time,current_limit,limit_left)
VALUES ('2024-03-23 18:00',1000,1000);

INSERT INTO service_monthly_limit (change_date_time,current_limit,limit_left)
VALUES ('2024-03-23 18:00',1000,1000);
