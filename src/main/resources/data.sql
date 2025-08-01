INSERT INTO customers (name, surname, tckn, role)
VALUES ('Atilla', 'Han', '11111111111','CUSTOMER'),
       ('Osman', 'Bey', '33333333333','CUSTOMER'),
       ('Atakan', 'Karaman', '22222222222', 'ADMIN');

INSERT INTO wallets (wallet_name, currency, active_for_shopping, active_for_withdraw, balance, usable_balance, customer_id, create_date, version)
VALUES ('Atilla hesap', 'TRY', TRUE, TRUE, 5000.00, 5000.00, 1, CURRENT_TIMESTAMP,0 ),
       ('admin hesap', 'TRY', TRUE, TRUE, 200.00, 200.00, 3, CURRENT_TIMESTAMP,0 ),
       ('osman hesap', 'TRY', FALSE, FALSE, 3000.00, 3000.00, 2, CURRENT_TIMESTAMP,0 ),
       ('osman hesap1', 'TRY', FALSE, TRUE, 2000.00, 2000.00, 2, CURRENT_TIMESTAMP,0 ),
       ('osman hesap2', 'TRY', TRUE, FALSE, 1000.00, 1000.00, 2, CURRENT_TIMESTAMP,0 );