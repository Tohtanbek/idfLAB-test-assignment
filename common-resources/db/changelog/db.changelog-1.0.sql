--liquibase formatted sql

--changeSet Anton D.:1


CREATE TABLE debit_transaction
(
    id BIGSERIAL PRIMARY KEY,
    account_from BIGINT,
    account_to BIGINT,
    sum NUMERIC(18,2),
    currency_code CHAR(3),
    expense_category CHAR(1),
    tr_date_time TIMESTAMP,
    time_zone_sec_offset INTEGER,
    limit_exceeded BOOLEAN
);

CREATE TABLE exchange_rate
(
    id BIGSERIAL PRIMARY KEY ,
    currency_code CHAR(3),
    actual_rate NUMERIC(18,2),
    access_date_time TIMESTAMP
);