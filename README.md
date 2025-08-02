# Digital Wallet Application

A Spring Boot-based digital wallet application that provides secure wallet management, and transaction processing.

## 🚀 Features

- **Wallet Management**: Create and manage digital wallets for customers
- **Transaction Processing**: Handle deposits, withdrawals, and approvals
- **Security**: Basic authentication and authorization
- **API Documentation**: Swagger/OpenAPI integration
- **Database**: H2 in-memory database for development
- **Exception Handling**: Global exception handling with proper error responses

## 🛠️ Technology Stack

- **Java**: 21
- **Spring Boot**: 3.4.5
- **Spring Security**: Basic authentication
- **Spring Data JPA**: Database operations
- **H2 Database**: In-memory database
- **Maven**: Build tool
- **Swagger/OpenAPI**: API documentation
- **JUnit**: Testing framework

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Git

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/atakankaraman40/ingcase.git
cd ingcase
```

### 2. Build the Application

```bash
# Clean and compile
mvn clean compile

# Or build
mvn clean install
```

### 3. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using the JAR file
java -jar target/digitalwallet-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## 📚 API Documentation

🔐 Authentication & Authorization

The application uses Role-Based Access Control (RBAC) to protect sensitive operations.

✅ Customers can only access endpoints related to their own wallets and transactions.
For example:

- Creating a wallet

- Viewing or listing their own transactions and wallets

- Making deposits and withdrawals

⚠️ Customers cannot update transaction status or access other users' data.

🔒 Transaction status updates (e.g., approving or denying) are restricted to users with the ADMIN role.

👑 Admins have full access to all endpoints and can perform any operation, including:

- Viewing all transactions and wallets

- Managing transaction statuses

- Making deposits and withdrawals

### Main Endpoints

#### Wallet Management
- `POST /api/v1/wallets` - Create a new wallet
- `GET /api/v1/wallets/{id}` - Lists all wallets for a given customer

#### Transaction Management
- `POST /api/v1/transactions/deposit` - Deposit money to wallet
- `POST /api/v1/transactions/withdraw` - Withdraw money from wallet
- `GET /api/v1/transactions/list` - List all transactions for a given customer and wallet
- `PATCH /api/v1/transactions/{id}` - Update transaction status

### Request/Response Examples

#### Create Wallet
```json
POST /api/v1/wallets
{
    "walletName" : "cuzdan",
    "currency" : "EUR",
    "activeForShopping": true,
    "activeForWithdraw": true,
    "customerId": 1
}
```

Response of Create Wallet
```json 
{
    "walletName": "cuzdan",
    "currency": "EUR",
    "activeForShopping": true,
    "activeForWithdraw": true,
    "balance": 0,
    "usableBalance": 0,
    "createDate": "2025-08-01 10:08:52"
}
```

#### Customer Wallet List
```json
GET /api/v1/wallets/1
```

Response of Customer Wallet List
```json 
[
    {
        "walletName": "Atilla hesap",
        "currency": "TRY",
        "activeForShopping": true,
        "activeForWithdraw": true,
        "balance": 5000.00,
        "usableBalance": 5000.00,
        "createDate": "2025-08-01 10:01:10"
    },
    {
        "walletName": "cuzdan",
        "currency": "EUR",
        "activeForShopping": true,
        "activeForWithdraw": true,
        "balance": 0.00,
        "usableBalance": 0.00,
        "createDate": "2025-08-01 10:08:52"
    }
]
```

#### Deposit
```json
POST /api/v1/transactions/deposit
{
    "amount": 2000,
    "walletId": 1,
    "customerId": 1,
    "oppositePartyType": "IBAN",
    "oppositeParty": "TR12312"
}
```

Response of Deposit
```json 
{
    "amount": 2000,
    "type": "DEPOSIT",
    "oppositePartyType": "IBAN",
    "oppositeParty": "TR12312",
    "status": "PENDING"
}
```

#### Withdraw
```json
POST /api/v1/transactions/withdraw
{
    "amount": 100,
    "walletId": 1,
    "customerId": 1,
    "oppositePartyType": "IBAN",
    "oppositeParty": "TR12312"
}
```

Response of Withdraw 
```json 
{
    "amount": 100,
    "type": "WITHDRAW",
    "oppositePartyType": "IBAN",
    "oppositeParty": "TR12312",
    "status": "APPROVED"
}
```

#### Customer Wallet Transactions
```json
GET /api/v1/transactions/list
```
**Headers:**
- `customerId`: 1  
- `walletId`: 1

Response of Customer Wallet Transactions
```json 
[
    {
        "amount": 2000.00,
        "type": "DEPOSIT",
        "oppositePartyType": "IBAN",
        "oppositeParty": "TR12312",
        "status": "PENDING"
    },
    {
        "amount": 100.00,
        "type": "WITHDRAW",
        "oppositePartyType": "IBAN",
        "oppositeParty": "TR12312",
        "status": "APPROVED"
    }
]
```

#### Update Transaction
```json
PATCH /api/v1/transactions/update/1
{
    "status": "APPROVED"
}
```

Response of Update Transaction
```json 
{
    "amount": 2000.00,
    "type": "DEPOSIT",
    "oppositePartyType": "IBAN",
    "oppositeParty": "TR12312",
    "status": "APPROVED"
}
```

## 🗄️ Database Schema

The application uses an H2 in-memory database with the following main entities:

- **Customer**: Customer information
- **Wallet**: Digital wallet details
- **Transaction**: Transaction records

### Database Configuration

- **Driver**: H2
- **URL**: jdbc:h2:mem:digitalwallet
- **Username**: ati
- **Password**: ati

**Database Schema Tables**

### Customers Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique customer identifier |
| `name` | VARCHAR(32) | NOT NULL | Customer's first name |
| `surname` | VARCHAR(32) | NOT NULL | Customer's last name |
| `tckn` | VARCHAR(32) | NOT NULL, UNIQUE | Turkish Citizenship Number |
| `role` | VARCHAR(32) | NOT NULL | Customer role (CUSTOMER/ADMIN) |

### Wallets Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique wallet identifier |
| `wallet_name` | VARCHAR(32) | NOT NULL | Name of the wallet |
| `currency` | VARCHAR(32) | NOT NULL | Currency type (TRY, EUR, USD) |
| `active_for_shopping` | BOOLEAN | NOT NULL | Whether wallet can be used for shopping |
| `active_for_withdraw` | BOOLEAN | NOT NULL | Whether wallet can be used for withdrawals |
| `balance` | DECIMAL(15, 2) | NOT NULL | Total balance in the wallet |
| `usable_balance` | DECIMAL(15, 2) | NOT NULL | Available balance for transactions |
| `customer_id` | BIGINT | NOT NULL, FOREIGN KEY | Reference to customers table |
| `create_date` | TIMESTAMP | NOT NULL | Wallet creation timestamp |
| `version` | INT | NOT NULL | Optimistic locking version |

### Transactions Table
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `wallet_id` | BIGINT | NOT NULL | Reference to wallets table |
| `amount` | DECIMAL(15, 2) | NOT NULL | Transaction amount |
| `type` | VARCHAR(32) | NOT NULL | Transaction type (DEPOSIT/WITHDRAW) |
| `opposite_party_type` | VARCHAR(32) | NOT NULL | Type of opposite party (IBAN, CARD, etc.) |
| `opposite_party` | VARCHAR(32) | NOT NULL | Identifier of opposite party |
| `status` | VARCHAR(32) | NOT NULL | Transaction status (PENDING/APPROVED/REJECTED) |
| `create_date` | TIMESTAMP | NOT NULL | Transaction creation timestamp |
| `version` | INT | NOT NULL | Optimistic locking version |

**Relationships:**
- `wallets.customer_id` → `customers.id` (Many-to-One)
- `transactions.wallet_id` → `wallets.id` (Many-to-One)

**schema.sql**
```json 
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
```

**data.sql**
```json 
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
```