
# Technical Requirements Document (TRD) - Expense Tracker API

## 1. Introduction

This document specifies the technical requirements and architecture for the Expense Tracker API. It is based on the features and user stories defined in the Product Requirements Document (PRD).

## 2. Technology Stack

*   **Framework:** Spring Boot 3.x
*   **Language:** Java 21
*   **Build Tool:** Maven
*   **Database:** H2 (In-memory for initial development), with a profile for PostgreSQL for production.
*   **Authentication:** Spring Security with JSON Web Tokens (JWT).
*   **Data Validation:** Jakarta Bean Validation.
*   **API Documentation:** OpenAPI 3 (Swagger).

## 3. Architecture

The application will follow a standard 3-tier architecture:

*   **Controller Layer:** Handles incoming HTTP requests, validates input, and delegates to the service layer.
*   **Service Layer:** Contains the core business logic of the application.
*   **Repository Layer:** Manages data access and interaction with the database using Spring Data JPA.

## 4. Data Models

The following entities will be defined:

### 4.1. User

Represents a user of the application.

| Field      | Type         | Constraints                |
|------------|--------------|----------------------------|
| `id`       | `Long`       | Primary Key, Auto-generated|
| `username` | `String`     | Not Null, Unique           |
| `email`      | `String`     | Not Null, Unique           |
| `password` | `String`     | Not Null, Encrypted        |

### 4.2. Expense

Represents a single expense entry.

| Field         | Type          | Constraints                |
|---------------|---------------|----------------------------|
| `id`          | `Long`        | Primary Key, Auto-generated|
| `user`        | `User`        | Many-to-One relationship   |
| `amount`      | `BigDecimal`  | Not Null, Positive         |
| `date`        | `LocalDate`   | Not Null                   |
| `description` | `String`      | Optional                   |
| `category`    | `Category`    | Many-to-One relationship   |

### 4.3. Category

Represents an expense category.

| Field  | Type     | Constraints                |
|--------|----------|----------------------------|
| `id`   | `Long`   | Primary Key, Auto-generated|
| `name` | `String` | Not Null, Unique per user  |
| `user` | `User`   | Many-to-One relationship   |

## 5. API Endpoints

The API will be versioned under `/api/v1`.

*   `POST /api/auth/register`
*   `POST /api/auth/login`
*   `GET /api/expenses`
*   `POST /api/expenses`
*   `GET /api/expenses/{id}`
*   `PUT /api/expenses/{id}`
*   `DELETE /api/expenses/{id}`
*   `GET /api/categories`
*   `POST /api/categories`
*   `PUT /api/categories/{id}`
*   `DELETE /api/categories/{id}`

## 6. Security

*   **Authentication:** JWT will be used for securing the API. The login endpoint will issue a token, which must be included in the `Authorization` header of subsequent requests (`Bearer <token>`).
*   **Password Encryption:** Passwords will be securely hashed using BCrypt.
*   **Authorization:** Endpoints will be protected based on the authenticated user. Users will only be able to access their own data.

## 7. Error Handling

The API will return standard HTTP status codes to indicate the outcome of a request. Error responses will be in a consistent JSON format:

```json
{
  "timestamp": "2023-10-27T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Expense with id 123 not found",
  "path": "/api/expenses/123"
}
```

## 8. Database Schema

*   **users** table corresponding to the `User` entity.
*   **expenses** table corresponding to the `Expense` entity, with a foreign key to the `users` and `categories` tables.
*   **categories** table corresponding to the `Category` entity, with a foreign key to the `users` table.
