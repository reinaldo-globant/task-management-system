# Task Management Backend

This is the backend API for the Task Management System, built with Spring Boot.

## Features

- **RESTful API**: Clean API design following REST principles
- **JWT Authentication**: Secure authentication and authorization
- **Task Management**: Full CRUD operations for tasks
- **User Management**: User registration and authentication
- **Data Validation**: Request validation and error handling
- **Cross-Origin Resource Sharing (CORS)**: Configured for frontend access

## Tech Stack

- **Spring Boot 3.x**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access
- **JWT**: JSON Web Token implementation
- **H2 Database**: In-memory database (can be replaced with MySQL, PostgreSQL, etc.)
- **Maven**: Dependency management and build
- **Java 17**: Programming language

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
  - Request: `{ "username": "user", "password": "password", "email": "user@example.com", "name": "User Name" }`
  - Response: `{ "id": 1, "username": "user", "email": "user@example.com", "name": "User Name" }`

- `POST /api/auth/login` - Authenticate and receive JWT token
  - Request: `{ "username": "user", "password": "password" }`
  - Response: `{ "token": "JWT_TOKEN", "type": "Bearer", "id": 1, "username": "user", "email": "user@example.com", "name": "User Name" }`

### Tasks

- `GET /api/tasks` - Get all tasks (admin only)
  - Response: `[{ "id": 1, "title": "Task title", "description": "Task description", "status": "TODO", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:00:00" }, ...]`

- `GET /api/tasks/my-tasks` - Get current user's tasks
  - Response: `[{ "id": 1, "title": "Task title", "description": "Task description", "status": "TODO", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:00:00" }, ...]`

- `GET /api/tasks/my-tasks/status/{status}` - Get current user's tasks by status
  - Response: `[{ "id": 1, "title": "Task title", "description": "Task description", "status": "TODO", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:00:00" }, ...]`

- `GET /api/tasks/{id}` - Get a specific task
  - Response: `{ "id": 1, "title": "Task title", "description": "Task description", "status": "TODO", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:00:00" }`

- `POST /api/tasks` - Create a new task
  - Request: `{ "title": "Task title", "description": "Task description", "status": "TODO" }`
  - Response: `{ "id": 1, "title": "Task title", "description": "Task description", "status": "TODO", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:00:00" }`

- `PUT /api/tasks/{id}` - Update an existing task
  - Request: `{ "title": "Updated title", "description": "Updated description", "status": "IN_PROGRESS" }`
  - Response: `{ "id": 1, "title": "Updated title", "description": "Updated description", "status": "IN_PROGRESS", "ownerId": 1, "createdAt": "2023-01-01T12:00:00", "updatedAt": "2023-01-01T12:30:00" }`

- `DELETE /api/tasks/{id}` - Delete a task
  - Response: `204 No Content`

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher

### Installation

```bash
# Clone the repository
git clone https://github.com/reinaldo-globant/task-management-system.git
cd task-management-system/task-backend

# Build the project
./mvnw clean install
```

### Running Locally

```bash
# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

## Configuration

Configuration is done through `application.properties` (or `application.yml`). Key configurations include:

- **Database Configuration**: Connection settings for the database
- **JWT Configuration**: Secret key and token expiration
- **CORS Configuration**: Allowed origins for cross-origin requests
- **Logging**: Logging levels and output

## Testing

```bash
# Run tests
./mvnw test
```

## Building for Production

```bash
# Package the application
./mvnw clean package
```

This creates a standalone JAR file in the `target` directory.

## Running with Docker

```bash
# Build Docker image
docker build -t task-backend .

# Run Docker container
docker run -p 8080:8080 task-backend
```

## Security

- All endpoints (except authentication) require a valid JWT token
- Passwords are hashed using BCrypt
- Role-based access control for administrative endpoints

## Data Model

The main entities in the system are:

### User
- id: Long
- username: String
- password: String (hashed)
- email: String
- name: String
- roles: Set<Role>

### Task
- id: Long
- title: String
- description: String
- status: Enum (TODO, IN_PROGRESS, DONE)
- owner: User
- createdAt: LocalDateTime
- updatedAt: LocalDateTime