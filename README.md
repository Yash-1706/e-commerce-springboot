# E-Commerce Backend API

A minimal e-commerce backend system built with Spring Boot and MongoDB.

## Features

- **Product Management**: Create and list products
- **Cart Management**: Add items to cart, view cart, clear cart
- **Order Management**: Create orders from cart, view orders, cancel orders
- **Payment Processing**: Mock payment service with webhook callbacks
- **User Management**: User registration and retrieval

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data MongoDB
- Lombok
- Maven

## Project Structure

```
com.example.ecommerce
├── controller/     # REST API endpoints
├── service/        # Business logic
├── repository/     # MongoDB repositories
├── model/          # Domain entities
├── dto/            # Data Transfer Objects
├── config/         # Configuration classes
├── exception/      # Exception handling
└── webhook/        # Webhook handlers
```

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- MongoDB Atlas account (or local MongoDB)

### Configuration

Update `src/main/resources/application.yaml` with your MongoDB connection:

```yaml
spring:
  data:
    mongodb:
      uri: your-mongodb-uri
      database: ecommerce
```

### Running the Application

1. **Start Mock Payment Service** (Port 8081):
```bash
cd mock-payment-service
mvn spring-boot:run
```

2. **Start E-Commerce API** (Port 8080):
```bash
cd ..
mvn spring-boot:run
```

## API Endpoints

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |

### Cart
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cart/add` | Add item to cart |
| GET | `/api/cart/{userId}` | Get user's cart |
| DELETE | `/api/cart/{userId}/clear` | Clear cart |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create order from cart |
| GET | `/api/orders/{orderId}` | Get order details |
| GET | `/api/orders/user/{userId}` | Get user's orders |
| POST | `/api/orders/{orderId}/cancel` | Cancel order |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/create` | Create payment |
| GET | `/api/payments/order/{orderId}` | Get payment by order |

### Webhooks
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/webhooks/payment` | Payment callback |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |

## Order Flow

1. Create products
2. Add items to cart
3. Create order from cart
4. Initiate payment
5. Webhook updates order status (after 3 seconds)
6. Check order status

## Sample Requests

### Create Product
```json
POST /api/products
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

### Add to Cart
```json
POST /api/cart/add
{
  "userId": "user123",
  "productId": "product-id",
  "quantity": 2
}
```

### Create Order
```json
POST /api/orders
{
  "userId": "user123"
}
```

### Create Payment
```json
POST /api/payments/create
{
  "orderId": "order-id",
  "amount": 100000.0
}
```

## License

MIT
