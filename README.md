# E-commerce Prototype

Spring Boot 3 / Java 21 prototype for learning the core e-commerce domain before splitting into microservices.

## Included

- Users with registration and login
- JWT authentication
- Products with public browsing and admin create/update/delete
- Orders with line items, stock reservation, totals, and status transitions
- PostgreSQL-ready Spring Data JPA model
- Docker Compose for local Postgres

## Run

Install Java 21 and Maven, then:

```bash
docker compose up -d postgres
mvn spring-boot:run
```

Or run the whole stack with Docker Compose:

```bash
docker compose up --build
```

## Default Data

On startup, the app creates:

- Admin: `admin@example.com` / `Admin123!`
- User: `user@example.com` / `User123!`
- Three sample products

## API Sketch

```http
POST /api/auth/register
POST /api/auth/login
GET  /api/products
GET  /api/products/{id}
POST /api/products          # ADMIN
PUT  /api/products/{id}     # ADMIN
DELETE /api/products/{id}   # ADMIN
POST /api/orders            # USER/ADMIN
GET  /api/orders            # ADMIN
GET  /api/orders/me         # USER/ADMIN
GET  /api/orders/{id}       # owner or ADMIN
PATCH /api/orders/{id}/status # ADMIN
```

Use `Authorization: Bearer <token>` for protected endpoints.

## Example Flow

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"user@example.com\",\"password\":\"User123!\"}"

curl http://localhost:8080/api/products

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d "{\"items\":[{\"productId\":1,\"quantity\":2}]}"
```
