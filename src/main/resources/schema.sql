DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS transactions;

create table customers
(
    id      BIGINT AUTO_INCREMENT  PRIMARY KEY,
    name    VARCHAR(32) NOT NULL,
    surname VARCHAR(32) NOT NULL,
    tckn    VARCHAR(32) NOT NULL UNIQUE,
    role    VARCHAR(32) NOT NULL
);

create table wallets
(
    id                  BIGINT AUTO_INCREMENT  PRIMARY KEY,
    wallet_name         VARCHAR(32) NOT NULL,
    currency            VARCHAR(32) NOT NULL,
    active_for_shopping BOOLEAN NOT NULL,
    active_for_withdraw BOOLEAN NOT NULL,
    balance             DECIMAL(15, 2) NOT NULL,
    usable_balance      DECIMAL(15, 2) NOT NULL,
    customer_id         BIGINT NOT NULL,
    create_date         TIMESTAMP NOT NULL,
    version             INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

create table transactions
(
    wallet_id           BIGINT NOT NULL,
    amount              DECIMAL(15, 2) NOT NULL,
    type                VARCHAR(32) NOT NULL,
    opposite_party_type VARCHAR(32) NOT NULL,
    opposite_party      VARCHAR(32) NOT NULL,
    status              VARCHAR(32) NOT NULL,
    create_date         TIMESTAMP NOT NULL,
    version             INT NOT NULL
);
