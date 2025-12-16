# Mutual Fund Management System

A comprehensive RESTful API for managing mutual funds with user authentication, transaction management, and administrative operations.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Error Handling](#error-handling)
- [Running Tests](#running-tests)
- [Code Quality](#code-quality)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

## Overview

The Mutual Fund Management System is a Spring Boot application that provides a complete solution for managing mutual fund investments. It supports user registration, fund transactions (buy/redeem), portfolio holdings tracking, and administrative operations for managing funds and users.

## Features

- **User Management**
  - User registration with role-based access control
  - Profile management
  - Secure authentication using Spring Security

- **Transaction Management**
  - Buy mutual fund units
  - Redeem mutual fund units
  - View transaction history with pagination
  - Real-time portfolio holdings with current valuations

- **Admin Operations**
  - Add and manage mutual funds
  - Update NAV (Net Asset Value)
  - View and manage all users
  - Delete funds and users

- **Additional Features**
  - Caching for improved performance
  - Comprehensive logging (JSON format for production)
  - API documentation with Swagger/OpenAPI
  - Health monitoring with Spring Boot Actuator
  - Code quality enforcement with Checkstyle
  - Automatic code formatting with Spotless

## Technology Stack

- **Backend:** Spring Boot 3.5.8
- **Java Version:** 17
- **Database:** H2 (in-memory for dev/test, file-based for production)
- **Security:** Spring Security with Basic Authentication
- **API Documentation:** SpringDoc OpenAPI 3
- **Build Tool:** Maven
- **Code Quality:** Checkstyle, Spotless (Google Java Format)
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **Code Coverage:** JaCoCo

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ id (PK)         │
│ username (UK)   │
│ password        │
│ role            │
└────────┬────────┘
         │
         │ 1
         │
         │ *
┌────────┴────────┐         ┌──────────────────┐
│   HOLDINGS      │    *    │  MUTUAL_FUNDS    │
├─────────────────┤────────┼──────────────────┤
│ id (PK)         │    1    │ fund_id (PK)     │
│ user_id (FK)    │         │ name             │
│ fund_id (FK)    │         │ nav              │
│ units           │         │ nav_date         │
│ total_value     │         └──────┬───────────┘
└────────┬────────┘                │
         │                          │
         │ 1                        │ 1
         │                          │
         │ *                        │ *
┌────────┴─────────────────────────┴────┐
│          TRANSACTIONS                 │
├───────────────────────────────────────┤
│ transaction_id (PK)                   │
│ user_id (FK)                          │
│ fund_id (FK)                          │
│ units                                 │
│ nav                                   │
│ type (BUY/REDEEM)                     │
│ transaction_date                      │
└───────────────────────────────────────┘
```

### Tables

#### 1. USERS

Stores user information for authentication and authorization.

| Column    | Type         | Constraints                    | Description                    |
|-----------|--------------|--------------------------------|--------------------------------|
| id        | BIGINT       | PRIMARY KEY, AUTO_INCREMENT    | Unique user identifier         |
| username  | VARCHAR(50)  | UNIQUE, NOT NULL               | User's login name              |
| password  | VARCHAR(255) | NOT NULL                       | Encrypted password (BCrypt)    |
| role      | VARCHAR(20)  | NOT NULL, DEFAULT 'USER'       | User role (USER/ADMIN)         |

**Indexes:**
- PRIMARY KEY: `id`
- UNIQUE INDEX: `username`

**JPA Entity:** `User.java`

---

#### 2. MUTUAL_FUNDS

Stores mutual fund information with daily NAV values.

| Column    | Type           | Constraints                         | Description                    |
|-----------|----------------|-------------------------------------|--------------------------------|
| fund_id   | BIGINT         | PRIMARY KEY, AUTO_INCREMENT         | Unique fund identifier         |
| name      | VARCHAR(255)   | NOT NULL                            | Fund name                      |
| nav       | DECIMAL(10,2)  | NOT NULL, CHECK (nav > 0)           | Net Asset Value per unit       |
| nav_date  | DATE           | NOT NULL                            | Date of NAV valuation          |

**Indexes:**
- PRIMARY KEY: `fund_id`
- UNIQUE INDEX: `(name, nav_date)` - Ensures one NAV per fund per day

**JPA Entity:** `MutualFund.java`

**Business Rules:**
- A fund can have only one NAV entry per date
- NAV must be positive (> 0.01)
- Historical NAV records are maintained

---

#### 3. HOLDINGS

Tracks current fund holdings for each user.

| Column       | Type           | Constraints                              | Description                    |
|--------------|----------------|------------------------------------------|--------------------------------|
| id           | BIGINT         | PRIMARY KEY, AUTO_INCREMENT              | Unique holding identifier      |
| user_id      | BIGINT         | FOREIGN KEY → users(id), NOT NULL        | Reference to user              |
| fund_id      | BIGINT         | FOREIGN KEY → mutual_funds(fund_id), NOT NULL | Reference to mutual fund |
| units        | DECIMAL(15,4)  | NOT NULL, CHECK (units >= 0)             | Number of units held           |
| total_value  | DECIMAL(15,2)  | NOT NULL, CHECK (total_value >= 0)       | Total investment value         |

**Indexes:**
- PRIMARY KEY: `id`
- UNIQUE INDEX: `(user_id, fund_id)` - One holding record per user per fund
- INDEX: `user_id` - For efficient user portfolio queries

**Foreign Keys:**
- `user_id` → `users(id)`
- `fund_id` → `mutual_funds(fund_id)`

**JPA Entity:** `Holding.java`

**Business Rules:**
- Each user can have only one holding record per fund
- Units and total_value are updated with each transaction
- Holdings with zero units are retained for historical tracking

---

#### 4. TRANSACTIONS

Records all buy and redeem transactions.

| Column            | Type           | Constraints                              | Description                    |
|-------------------|----------------|------------------------------------------|--------------------------------|
| transaction_id    | BIGINT         | PRIMARY KEY, AUTO_INCREMENT              | Unique transaction identifier  |
| user_id           | BIGINT         | FOREIGN KEY → users(id), NOT NULL        | User who made the transaction  |
| fund_id           | BIGINT         | FOREIGN KEY → mutual_funds(fund_id), NOT NULL | Fund involved in transaction |
| units             | DECIMAL(15,4)  | NOT NULL, CHECK (units > 0)              | Number of units transacted     |
| nav               | DECIMAL(10,2)  | NOT NULL, CHECK (nav > 0)                | NAV at transaction time        |
| type              | VARCHAR(10)    | NOT NULL, CHECK (type IN ('BUY','REDEEM')) | Transaction type             |
| transaction_date  | TIMESTAMP      | NOT NULL, DEFAULT CURRENT_TIMESTAMP      | When transaction occurred      |

**Indexes:**
- PRIMARY KEY: `transaction_id`
- INDEX: `user_id` - For user transaction history
- INDEX: `fund_id` - For fund transaction history
- INDEX: `transaction_date` - For date-based queries

**Foreign Keys:**
- `user_id` → `users(id)`
- `fund_id` → `mutual_funds(fund_id)`

**JPA Entity:** `Transaction.java`

**Business Rules:**
- Transactions are immutable once created
- BUY transactions increase holdings
- REDEEM transactions decrease holdings
- NAV is captured at transaction time for historical accuracy

---

### Relationships

#### One-to-Many Relationships

1. **User → Holdings** (1:N)
   - One user can have multiple holdings (different funds)
   - Mapped via: `users.id` → `holdings.user_id`
   - Fetch: `LAZY`

2. **User → Transactions** (1:N)
   - One user can have multiple transactions
   - Mapped via: `users.id` → `transactions.user_id`
   - Fetch: `LAZY`

3. **MutualFund → Holdings** (1:N)
   - One fund can be held by multiple users
   - Mapped via: `mutual_funds.fund_id` → `holdings.fund_id`
   - Fetch: `LAZY`

4. **MutualFund → Transactions** (1:N)
   - One fund can have multiple transactions
   - Mapped via: `mutual_funds.fund_id` → `transactions.fund_id`
   - Fetch: `LAZY`

### JPA Mapping Details

#### Cascade Operations
- **No cascade operations** are defined to prevent accidental data deletion
- Parent entities (User, MutualFund) can exist without children
- Child entities (Holdings, Transactions) require parent existence

#### Fetch Strategies
- All relationships use `FetchType.LAZY` for performance optimization
- Data is loaded only when explicitly accessed

#### Orphan Removal
- Not enabled - Holdings and Transactions are preserved for audit trail

### Database Constraints Summary

| Constraint Type        | Table         | Description                              |
|------------------------|---------------|------------------------------------------|
| Primary Key            | users         | id                                       |
| Unique                 | users         | username                                 |
| Primary Key            | mutual_funds  | fund_id                                  |
| Unique                 | mutual_funds  | (name, nav_date)                         |
| Primary Key            | holdings      | id                                       |
| Unique                 | holdings      | (user_id, fund_id)                       |
| Foreign Key            | holdings      | user_id → users(id)                      |
| Foreign Key            | holdings      | fund_id → mutual_funds(fund_id)          |
| Primary Key            | transactions  | transaction_id                           |
| Foreign Key            | transactions  | user_id → users(id)                      |
| Foreign Key            | transactions  | fund_id → mutual_funds(fund_id)          |
| Check                  | mutual_funds  | nav > 0                                  |
| Check                  | holdings      | units >= 0, total_value >= 0             |
| Check                  | transactions  | units > 0, nav > 0                       |
| Check                  | transactions  | type IN ('BUY', 'REDEEM')                |

### Sample Data Flow

#### 1. User Registration
```sql
INSERT INTO users (username, password, role) 
VALUES ('john_doe', '$2a$10$...', 'USER');
```

#### 2. Admin Adds Mutual Fund
```sql
INSERT INTO mutual_funds (name, nav, nav_date) 
VALUES ('ABC Growth Fund', 150.50, '2025-12-16');
```

#### 3. User Buys Units
```sql
-- Create transaction record
INSERT INTO transactions (user_id, fund_id, units, nav, type, transaction_date)
VALUES (1, 1, 10.0000, 150.50, 'BUY', '2025-12-16 10:30:00');

-- Update or create holding
INSERT INTO holdings (user_id, fund_id, units, total_value)
VALUES (1, 1, 10.0000, 1505.00)
ON DUPLICATE KEY UPDATE 
  units = units + 10.0000,
  total_value = total_value + 1505.00;
```

#### 4. User Redeems Units
```sql
-- Create transaction record
INSERT INTO transactions (user_id, fund_id, units, nav, type, transaction_date)
VALUES (1, 1, 5.0000, 155.75, 'REDEEM', '2025-12-17 14:20:00');

-- Update holding
UPDATE holdings 
SET units = units - 5.0000,
    total_value = total_value - 778.75
WHERE user_id = 1 AND fund_id = 1;
```

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 17 or higher
- Maven 3.6+ or use the included Maven wrapper
- Git (optional, for cloning the repository)

## Setup Instructions

### 1. Clone or Download the Project

```bash
git clone <repository-url>
cd mutual-fund-app-v1
```

### 2. Database Configuration

The application uses H2 database with different configurations for each profile:

**Default/Test Profile (in-memory):**
- Database: H2 in-memory
- URL: `jdbc:h2:mem:mutualfunddb`
- Console: http://localhost:8080/h2-console
- Username: `sa`
- Password: (empty)

**Production Profile (file-based):**
- Database: H2 file-based
- URL: `jdbc:h2:file:./data/mutualfunddb`
- Data persists in `./data` directory

### 3. Application Profiles

The application supports multiple Spring profiles:

- **test** (default): Minimal logging, security disabled for testing
- **prod**: File-based H2, enhanced logging, full security

To change the active profile, edit `src/main/resources/application.properties`:
```properties
spring.profiles.active=test
```

Or pass as a runtime argument:
```bash
java -jar target/mutual-fund-management-1.0.0.jar --spring.profiles.active=prod
```

### 4. Database Password Configuration

The application uses an environment variable for database password. Set it before running:

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD="your_password"
```

**Windows (Command Prompt):**
```cmd
set DB_PASSWORD=your_password
```

**Linux/Mac:**
```bash
export DB_PASSWORD=your_password
```

## Running the Application

### Using Maven

**Windows (PowerShell):**
```powershell
# Set database password (optional for H2)
$env:DB_PASSWORD=""

# Clean and build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

**Linux/Mac:**
```bash
# Set database password (optional for H2)
export DB_PASSWORD=""

# Clean and build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using Java

**Windows (PowerShell):**
```powershell
# Set database password (optional for H2)
$env:DB_PASSWORD=""

# Build the JAR
mvn clean package

# Run the JAR
java -jar target/mutual-fund-management-1.0.0.jar
```

**Linux/Mac:**
```bash
# Set database password (optional for H2)
export DB_PASSWORD=""

# Build the JAR
mvn clean package

# Run the JAR
java -jar target/mutual-fund-management-1.0.0.jar
```

### Run with specific profile

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD=""
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Linux/Mac:**
```bash
export DB_PASSWORD=""
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

The application will start on **http://localhost:8080**

### Default Credentials

- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `ADMIN`

## API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

### Postman Collection

Import the Postman collection for easy API testing:
- **File:** `Mutual-Fund-API.postman_collection.json`
- Pre-configured with base URL and authentication
- Includes all API endpoints with sample requests

### API Endpoints

#### User Management
- `POST /api/v1/users/register` - Register new user (public)
- `GET /api/v1/users/{userId}` - Get user profile (authenticated)

#### Transactions
- `POST /api/v1/users/{userId}/buy` - Buy mutual fund units
- `POST /api/v1/users/{userId}/redeem` - Redeem mutual fund units
- `GET /api/v1/users/{userId}/holdings` - View current holdings
- `GET /api/v1/users/{userId}/transactions` - View transaction history

#### Admin Operations (ADMIN role required)
- `POST /api/v1/admin/funds` - Add new mutual fund
- `PUT /api/v1/admin/funds/{fundId}/nav` - Update fund NAV
- `GET /api/v1/admin/funds` - List all funds
- `DELETE /api/v1/admin/funds/{fundId}` - Delete fund
- `GET /api/v1/admin/users` - List all users
- `DELETE /api/v1/admin/users/{userId}` - Delete user

#### Health & Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator` - List all actuator endpoints

## Error Handling

The application uses a robust error handling system with `ErrorCode` enum for type-safe error management and automatic message resolution.

### Components

#### ErrorCode Enum
Type-safe enumeration representing all possible error codes in the application:

```java
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE"),
    TRANSACTION_FAILED("TRANSACTION_FAILED"),
    // ... etc
}
```

#### Custom Exceptions

**BusinessException** - For business rule violations:
```java
// Throw with error code only
throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);

// Throw with error code and additional context
throw new BusinessException(ErrorCode.INVALID_AMOUNT, "Amount: -100");
```

**ResourceNotFoundException** - When requested resource not found:
```java
throw new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User ID: 123");
```

### Error Response Format

All errors return a consistent JSON structure:

```json
{
    "userMessage": "Insufficient balance to complete this transaction.",
    "devMessage": "User's wallet balance is less than the required transaction amount. - Required: 1000, Available: 500",
    "errorCode": "INSUFFICIENT_BALANCE",
    "timestamp": "2025-12-16T10:30:00",
    "path": "/api/v1/users/1/buy"
}
```

### Available Error Codes

| Error Code | Description |
|------------|-------------|
| `RESOURCE_NOT_FOUND` | Generic resource not found |
| `USER_NOT_FOUND` | User not found |
| `MUTUAL_FUND_NOT_FOUND` | Mutual fund not found |
| `HOLDING_NOT_FOUND` | Holding not found |
| `TRANSACTION_NOT_FOUND` | Transaction not found |
| `INSUFFICIENT_BALANCE` | Insufficient balance |
| `INSUFFICIENT_UNITS` | Insufficient units to redeem |
| `INVALID_AMOUNT` | Invalid transaction amount |
| `INVALID_UNITS` | Invalid units |
| `DUPLICATE_USER` | Username already exists |
| `DUPLICATE_FUND` | Mutual fund already exists |
| `VALIDATION_ERROR` | Input validation error |
| `ACCESS_DENIED` | Access denied |
| `AUTHENTICATION_FAILED` | Authentication failed |
| `UNAUTHORIZED` | Unauthorized access |
| `INTERNAL_SERVER_ERROR` | Internal server error |
| `BAD_REQUEST` | Bad request |
| `NAV_UPDATE_FAILED` | NAV update failed |
| `TRANSACTION_FAILED` | Transaction failed |

### Usage in Service Layer

```java
@Service
public class UserService {
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.USER_NOT_FOUND, 
                "User ID: " + userId
            ));
    }
    
    public void buyUnits(Long userId, BigDecimal amount) {
        User user = getUserById(userId);
        
        if (user.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(
                ErrorCode.INSUFFICIENT_BALANCE,
                String.format("Required: %s, Available: %s", amount, user.getBalance())
            );
        }
        
        // Process transaction...
    }
}
```

### Benefits

- **Type Safety**: Enum prevents typos and provides compile-time checking
- **Consistency**: All errors follow the same structure
- **Maintainability**: Error messages centralized in `error-messages.json`
- **Context**: Custom messages can be appended to standard messages
- **IDE Support**: Auto-completion and refactoring support
- **Documentation**: Clear enum values document all error scenarios

For detailed usage examples and migration guide, see [docs/ERROR_HANDLING_GUIDE.md](docs/ERROR_HANDLING_GUIDE.md)

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage Report

```bash
mvn clean test jacoco:report
```

View the coverage report at: `target/site/jacoco/index.html`

### Run Specific Test Class

```bash
mvn test -Dtest=UserServiceTest
```

### Test Structure

- **Unit Tests:** Service layer tests with Mockito
- **Integration Tests:** Controller tests with MockMvc
- **Test Coverage:** JaCoCo generates detailed coverage reports

**Note:** Controller tests are currently disabled. Remove `@Disabled` annotation to enable them.

## Code Quality

### Run Checkstyle

Check code style violations:
```bash
mvn checkstyle:check
```

### Format Code

Auto-format all Java files:
```bash
mvn spotless:apply
```

Check formatting without applying:
```bash
mvn spotless:check
```

### Code Quality Standards

- **Line Length:** Max 120 characters
- **Method Length:** Max 150 lines
- **File Length:** Max 1000 lines
- **Naming:** camelCase for variables/methods, PascalCase for classes
- **Imports:** No star imports, organized by package
- **Formatting:** Google Java Format (AOSP style)

## Project Structure

```
mutual-fund-app-v1/
├── src/
│   ├── main/
│   │   ├── java/com/mutualfund/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── exception/        # Exception handling
│   │   │   ├── model/
│   │   │   │   ├── entity/       # JPA entities
│   │   │   │   ├── request/      # Request DTOs
│   │   │   │   └── response/     # Response DTOs
│   │   │   ├── repository/       # Data repositories
│   │   │   ├── service/          # Business logic
│   │   │   └── MutualFundApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-test.properties
│   │       ├── application-prod.properties
│   │       ├── logback-spring.xml
│   │       └── error-messages.json
│   └── test/
│       └── java/com/mutualfund/  # Unit and integration tests
├── docs/
│   ├── Mutual-Fund-API.postman_collection.json
│   └── ERROR_HANDLING_GUIDE.md   # Error handling documentation
├── checkstyle.xml                # Checkstyle configuration
├── checkstyle-suppressions.xml   # Checkstyle suppressions
├── pom.xml                       # Maven configuration
└── README.md
```

## Contributing

### Development Workflow

1. Create a feature branch
2. Make your changes
3. Run tests: `mvn test`
4. Check code style: `mvn checkstyle:check`
5. Format code: `mvn spotless:apply`
6. Commit and push changes
7. Create a pull request

### Coding Standards

- Follow the Checkstyle rules defined in `checkstyle.xml`
- Write unit tests for new features
- Maintain test coverage above 80%
- Use meaningful commit messages
- Document public APIs with JavaDoc

## Troubleshooting

### Common Issues

**Port 8080 already in use:**
```properties
# Change port in application.properties
server.port=8081
```

**H2 Console not accessible:**
- Ensure `spring.h2.console.enabled=true` in application.properties
- Check if running with test profile

**Build failures:**
```bash
# Clean and rebuild
mvn clean install -DskipTests
```

**Authentication issues:**
- Verify credentials: admin/admin123
- Check if security is disabled (test profile)

## License

This project is for educational purposes.

## Contact

For questions or support, please open an issue in the repository.

---

**Version:** 1.0.0  
**Last Updated:** December 2025
