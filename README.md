# 🛒 E-Commerce Store API

A production-ready, transactional e-commerce backend system built with Spring Boot, featuring comprehensive order management, inventory tracking, secure authentication, and payment processing simulation.

### 🔐 Authentication & Authorization
- JWT-based authentication with role-based access control
- User registration and login
- Secure password hashing with BCrypt
- Role-based permissions (USER, SELLER)

### 🛍️ Product Management
- CRUD operations for products
- Product categorization and pricing
- Image management for products
- Seller-specific product listings

### 🛒 Shopping Cart
- Add/remove items from cart
- Real-time inventory validation
- Cart persistence across sessions
- Quantity management with stock checks

### 📦 Order Management
- Transactional order placement with inventory reservation
- Order lifecycle management (CREATED → PAYMENT_PENDING → PAID → PROCESSING → SHIPPED → DELIVERED)
- Order history and tracking
- Order cancellation and refund support

### 💳 Payment Processing
- Simulated payment gateway (CARD, UPI, COD)
- Transaction ID generation
- Payment status tracking (PENDING, SUCCESS, FAILED, REFUNDED)
- Automatic inventory rollback on payment failure

### 📊 Inventory Management
- Real-time stock tracking with optimistic locking
- Prevent overselling with reserved quantity management
- Automatic inventory restoration on order cancellation
- Stock validation before order placement

### 🛡️ Security Features
- JWT token-based authentication
- CORS configuration
- Input validation and sanitization
- SQL injection prevention with JPA
- XSS protection

### 🚨 Error Handling
- Global exception handling with custom error responses
- Custom exceptions for business logic (InsufficientStockException, PaymentFailedException, etc.)
- Structured error responses with HTTP status codes
- Transaction rollback on failures

## 🛠️ Tech Stack

### Backend
- **Java 25**
- **Spring Boot 4.0.3**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **JWT** - Token-based authentication
- **Maven** - Dependency management

### Key Dependencies
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-data-jpa` - ORM
- `postgresql` - Database driver
- `jjwt` - JWT token handling
- `spring-boot-starter-validation` - Input validation

## 🏗️ Architecture

### Layered Architecture
```
┌─────────────────┐
│   Controllers   │  ← REST API endpoints
├─────────────────┤
│    Services     │  ← Business logic
├─────────────────┤
│  Repositories   │  ← Data access layer
├─────────────────┤
│    Models       │  ← JPA entities
└─────────────────┘
```

### Key Design Patterns
- **Service Layer Pattern** - Business logic encapsulation
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Data transfer objects
- **Exception Handling** - Centralized error management
- **Transactional Boundaries** - ACID compliance for critical operations

## 🗄️ Database Schema

### Core Entities

#### User
```sql
- id (PK)
- email (Unique)
- password (Hashed)
- role (USER/SELLER)
- created_at, updated_at
```

#### Product
```sql
- id (PK)
- seller_id (FK → User)
- name
- description
- price
- category
- created_at, updated_at
```

#### Inventory
```sql
- product_id (PK, FK → Product)
- available_quantity
- reserved_quantity
- version (Optimistic locking)
```

#### Cart & CartItem
```sql
Cart:
- id (PK)
- user_id (FK → User)

CartItem:
- id (PK)
- cart_id (FK → Cart)
- product_id (FK → Product)
- quantity
```

#### Order & OrderItem
```sql
Order:
- id (PK)
- user_id (FK → User)
- status (Enum: CREATED, PAYMENT_PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED)
- total_amount
- payment_method
- created_at, updated_at

OrderItem:
- id (PK)
- order_id (FK → Order)
- product_id (FK → Product)
- quantity
- purchase_price
```

#### Payment
```sql
- id (PK)
- order_id (FK → Order, Unique)
- transaction_id (Unique)
- amount
- payment_method (CARD/UPI/COD)
- status (Enum: PENDING, SUCCESS, FAILED, REFUNDED)
- payment_date
- created_at, updated_at
```

## 🔗 API Endpoints

### Authentication
```
POST /api/auth/register     - User registration
POST /api/auth/login        - User login
```

### Products
```
GET    /api/products        - List all products
GET    /api/products/{id}   - Get product by ID
POST   /api/products        - Create product (SELLER only)
PUT    /api/products/{id}   - Update product (SELLER only)
DELETE /api/products/{id}   - Delete product (SELLER only)
```

### Cart Management
```
GET    /api/cart            - Get user's cart
POST   /api/cart            - Add item to cart
DELETE /api/cart/{id}       - Remove item from cart
POST   /api/cart/checkout   - Checkout cart (creates order)
```

### Orders
```
GET    /api/orders          - Get user's orders
```

### Payments
```
POST   /api/payments/process/{orderId}  - Process payment
GET    /api/payments/{transactionId}    - Get payment details
```

## 🚀 Getting Started

### Prerequisites
- Java 25+
- Maven 3.6+
- PostgreSQL 12+
- Git

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd ecommerce-system/ecommerce-store
```

2. **Configure Database**
```sql
CREATE DATABASE ecommerce_db;
-- Update connection details in application.properties if needed
```

3. **Build the application**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## ⚙️ Configuration

### Application Properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

### Environment Variables
```bash
DB_URL=jdbc:postgresql://localhost:5432/ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

## 📝 Usage Examples

### 1. User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 2. User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 3. Add Product to Cart
```bash
curl -X POST http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### 4. Checkout Cart
```bash
curl -X POST http://localhost:8080/api/cart/checkout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Process Payment
```bash
curl -X POST http://localhost:8080/api/payments/process/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "CARD",
    "amount": 99.99
  }'
```

## 🔒 Security

### Authentication Flow
1. User registers/logs in → Receives JWT token
2. Token included in `Authorization: Bearer <token>` header
3. Server validates token on protected endpoints
4. Role-based access control for SELLER operations

### Security Best Practices Implemented
- Password hashing with BCrypt
- JWT token expiration
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection through proper encoding

## 🚨 Error Handling

### Custom Exceptions
- `OrderNotFoundException` - 404
- `PaymentFailedException` - 402 (Payment Required)
- `InsufficientStockException` - 400
- `RuntimeException` - 400/500

### Error Response Format
```json
{
  "message": "Error description",
  "success": false,
  "status": 400
}
```

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Manual Testing
Use tools like Postman or curl to test API endpoints as documented above.

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:25-jdk-slim
COPY target/ecommerce-store-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Production Checklist
- [ ] Configure production database
- [ ] Set secure JWT secret
- [ ] Enable HTTPS
- [ ] Configure logging
- [ ] Set up monitoring
- [ ] Configure CORS for frontend domain
- [ ] Set up CI/CD pipeline

### 🎯 Project Goals
- Demonstrate proficiency in Spring Boot ecosystem
- Implement complex business logic with transactional integrity
- Build secure, scalable REST APIs
- Apply software engineering best practices
- Create comprehensive documentation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
