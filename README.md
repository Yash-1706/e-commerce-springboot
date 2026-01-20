# E-Commerce Backend API

A robust, minimal e-commerce backend built with **Spring Boot** and **MongoDB**. This project serves as the "invisible engine" for an online store, handling product management, shopping carts, orders, and payment processing.

## 🚀 Project Overview

This API manages the core lifecycle of an e-commerce transaction:
1.  **Product Discovery**: Listing and managing inventory.
2.  **Shopping Cart**: persistent cart management for users.
3.  **Order Processing**: Converting carts to orders.
4.  **Payments**: Mock payment integration with webhook callbacks for asynchronous status updates.

Built with **Clean Architecture** principles to ensure scalability and maintainability.

## ✨ Key Features

*   **📦 Product Management**: CRUD operations for products with stock tracking.
*   **🛒 Smart Cart**: Add/remove items, auto-calculation of totals.
*   **💳 Mock Payment System**: Simulates real-world payment gateways with webhook confirmations.
*   **⚡ Async Webhooks**: Handles payment success callbacks to update order status automatically.
*   **🔒 Secure Configuration**: Sensitive data handling patterns.

## 🛠️ Tech Stack

*   **Java 17**
*   **Spring Boot 3.2.0**
*   **Spring Data MongoDB**
*   **Lombok**
*   **Maven**

## 🏗️ Architecture

The project follows a layered architecture to separate concerns:
*   **Controller**: Handles incoming REST requests.
*   **Service**: Contains business logic (e.g., order validation, stock checking).
*   **Repository**: Data access layer for MongoDB.
*   **DTO**: Data Transfer Objects for secure API communication.

## 🏁 Getting Started

### Prerequisites
*   Java 17+
*   Maven 3.6+
*   MongoDB (Local or Atlas)

### 1. Configure Database
Update `src/main/resources/application.yaml` (rename `.example` if needed):
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ecommerce
```

### 2. Run the Services
**Step 1: Start Mock Payment Service** (Port 8081)
```bash
cd mock-payment-service
mvn spring-boot:run
```

**Step 2: Start Main API** (Port 8080)
```bash
# In a new terminal, from project root
mvn spring-boot:run
```

## 🔌 API Endpoints

| Module | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Products** | `GET` | `/api/products` | List all products |
| | `POST` | `/api/products` | Create a product |
| **Cart** | `POST` | `/api/cart/add` | Add item to cart |
| | `GET` | `/api/cart/{userId}` | View user cart |
| **Orders** | `POST` | `/api/orders` | Checkout cart to order |
| | `GET` | `/api/orders/user/{userId}` | List user orders |
| **Payments** | `POST` | `/api/payments/create` | Initiate payment |

## 📜 License
MIT
