# Task Service

Task management microservice for the Task Management System.

## Features

- **RESTful API**: Clean API design following REST principles
- **Task Management**: Full CRUD operations for tasks
- **JWT Authentication**: Secure API access through token validation
- **User Context**: Tasks are associated with authenticated users
- **Status Management**: Tasks can be in different states (TODO, IN_PROGRESS, DONE)
- **Data Validation**: Request validation and error handling
- **Cross-Origin Resource Sharing (CORS)**: Configured for frontend access
- **PostgreSQL Database**: Persistent storage for task data

## Tech Stack

- **Spring Boot 3.x**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access
- **Spring WebFlux WebClient**: Communication with User Service
- **PostgreSQL**: Relational database for persistent storage
- **Maven**: Dependency management and build
- **Java 17**: Programming language

## API Endpoints

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
- PostgreSQL 14 or higher
- User Service running on port 8081

### Configuration

Configuration is done through `application.properties`. Key configurations include:

- **Database Configuration**: Connection settings for PostgreSQL
- **User Service Configuration**: URL for the User Service
- **Server Port**: Set to 8080 by default

### Running Locally

```bash
# Build the project
./mvnw clean install

# Run the service
./mvnw spring-boot:run
```

The service will be available at `http://localhost:8080`.

### Running with Docker

```bash
# Build Docker image
docker build -t task-service .

# Run Docker container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/taskdb \
  -e USER_SERVICE_URL=http://host.docker.internal:8081 \
  task-service
```

## Security

- All endpoints require a valid JWT token (validated through the User Service)
- Users can only access their own tasks
- Admin users can access all tasks

## Docker Compose

This service is part of a larger microservices architecture and can be run using the docker-compose.yml file in the parent directory.

```bash
# From the parent directory
docker-compose up
```

## Data Model

### Task
- id: Long
- title: String
- description: String
- status: String (TODO, IN_PROGRESS, DONE)
- ownerId: Long
- ownerName: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime