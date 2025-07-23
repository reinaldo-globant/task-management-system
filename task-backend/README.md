# Task Management REST API

A Spring Boot application that provides RESTful services for managing tasks with different statuses and owners.

## Features

- Create, read, update, and delete tasks
- Filter tasks by status (TODO, IN_PROGRESS, DONE)
- Filter tasks by owner
- Track task status changes
- User authentication and authorization with JWT
- Stateless service architecture
- In-memory H2 database for data storage
- OpenAPI/Swagger documentation for easy API exploration

## API Endpoints

### Authentication Endpoints

| Method | URL                                | Description                      |
|--------|-----------------------------------|----------------------------------|
| POST   | /api/auth/signup                  | Register a new user              |
| POST   | /api/auth/signin                  | Authenticate and get JWT token   |

### Task Endpoints (Requires Authentication)

| Method | URL                                | Description                      |
|--------|-----------------------------------|----------------------------------|
| GET    | /api/tasks                        | Get all tasks                    |
| GET    | /api/tasks/{id}                   | Get task by ID                   |
| GET    | /api/tasks/status/{status}        | Get tasks by status              |
| GET    | /api/tasks/owner/{ownerId}        | Get tasks by owner ID            |
| GET    | /api/tasks/my-tasks               | Get tasks for the current user   |
| GET    | /api/tasks/my-tasks/status/{status}| Get current user's tasks by status |
| GET    | /api/tasks/status/{status}/owner/{ownerId} | Get tasks by status and owner ID |
| POST   | /api/tasks                        | Create a new task                |
| PUT    | /api/tasks/{id}                   | Update an existing task (owner only) |
| DELETE | /api/tasks/{id}                   | Delete a task (owner only)       |

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven

### Steps to Run
1. Clone the repository
2. Navigate to the project directory
3. Run `mvn spring-boot:run`
4. The application will start on http://localhost:8080

## H2 Database Console

The H2 console is enabled and can be accessed at http://localhost:8080/h2-console with the following settings:
- JDBC URL: jdbc:h2:mem:taskdb
- Username: sa
- Password: (leave empty)

## API Documentation

The API documentation is available through Swagger UI:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Example API Request

### Authentication
```
POST /api/auth/signin
Content-Type: application/json

{
  "username": "alice",
  "password": "password"
}
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "fullName": "Alice Smith"
}
```

### Registration
```
POST /api/auth/signup
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User"
}
```

Response:
```json
{
  "message": "User registered successfully!"
}
```

### Create a Task
```
POST /api/tasks
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "title": "Implement user profile",
  "description": "Create UI and backend for user profile management",
  "status": "TODO"
}
```

### Update Task Status
```
PUT /api/tasks/1
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "title": "Implement user profile",
  "description": "Create UI and backend for user profile management",
  "status": "IN_PROGRESS"
}
```