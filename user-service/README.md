# User Service

Authentication and user management microservice for the Task Management System.

## Features

- **RESTful API**: Clean API design following REST principles
- **JWT Authentication**: Secure authentication and authorization
- **User Management**: User registration and authentication
- **Role-Based Access Control**: User and admin roles
- **Data Validation**: Request validation and error handling
- **Cross-Origin Resource Sharing (CORS)**: Configured for frontend access
- **PostgreSQL Database**: Persistent storage for user data

## Tech Stack

- **Spring Boot 3.x**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access
- **JWT**: JSON Web Token implementation
- **PostgreSQL**: Relational database for persistent storage
- **Maven**: Dependency management and build
- **Java 17**: Programming language

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
  - Request: `{ "username": "user", "password": "password", "email": "user@example.com", "name": "User Name" }`
  - Response: `{ "message": "User registered successfully!" }`

- `POST /api/auth/login` - Authenticate and receive JWT token
  - Request: `{ "username": "user", "password": "password" }`
  - Response: `{ "token": "JWT_TOKEN", "type": "Bearer", "id": 1, "username": "user", "email": "user@example.com", "name": "User Name", "roles": ["ROLE_USER"] }`

### Users

- `GET /api/users` - Get all users (admin only)
  - Response: `[{ "id": 1, "username": "user", "email": "user@example.com", "name": "User Name" }, ...]`

- `GET /api/users/{id}` - Get user by ID
  - Response: `{ "id": 1, "username": "user", "email": "user@example.com", "name": "User Name" }`

- `POST /api/users/validate` - Validate a JWT token
  - Request: Send JWT token in Authorization header
  - Response: `{ "message": "Token is valid" }` or `{ "message": "Invalid token" }`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- PostgreSQL 14 or higher

### Configuration

Configuration is done through `application.properties`. Key configurations include:

- **Database Configuration**: Connection settings for PostgreSQL
- **JWT Configuration**: Secret key and token expiration
- **CORS Configuration**: Allowed origins for cross-origin requests
- **Server Port**: Set to 8081 by default

### Running Locally

```bash
# Build the project
./mvnw clean install

# Run the service
./mvnw spring-boot:run
```

The service will be available at `http://localhost:8081`.

### Running with Docker

```bash
# Build Docker image
docker build -t user-service .

# Run Docker container
docker run -p 8081:8081 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/userdb user-service
```

## Security

- All endpoints (except authentication) require a valid JWT token
- Passwords are hashed using BCrypt
- Role-based access control for administrative endpoints

## Docker Compose

This service is part of a larger microservices architecture and can be run using the docker-compose.yml file in the parent directory.

```bash
# From the parent directory
docker-compose up
```

## Data Model

### User
- id: Long
- username: String
- password: String (hashed)
- email: String
- name: String
- roles: Set<Role>

### Role
- id: Integer
- name: ERole (ROLE_USER, ROLE_ADMIN)