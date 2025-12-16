# Mutual Fund Management System

A comprehensive RESTful API for managing mutual funds with user authentication, transaction management, and administrative operations.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
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

## Running the Application

### Using Maven

```bash
# Clean and build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

### Using Java

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/mutual-fund-management-1.0.0.jar
```

### Run with specific profile

```bash
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
