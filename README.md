# Task Management System

A full-stack task management application with a Spring Boot backend and React frontend.

![Task Management System](https://user-images.githubusercontent.com/your-username/task-management-system/main/screenshots/taskboard.png)

## Project Overview

This task management system provides a modern, responsive interface for organizing tasks across different status columns (To Do, In Progress, Done). It features:

- **User Authentication**: Secure login and registration system using JWT tokens
- **Task Board**: Visual Kanban-style board with drag-and-drop functionality
- **Task Management**: Create, read, update, and delete tasks
- **Side Modal**: Detailed task view and editing through a slide-in panel
- **Responsive Design**: Works on desktop and mobile devices

## Project Structure

This repository contains two main components:

```
task-management-system/
├── docker-compose.yml         # Docker configuration for full-stack deployment
├── task-backend/              # Spring Boot API backend
│   ├── src/                   # Java source code
│   ├── pom.xml                # Maven dependencies
│   └── Dockerfile             # Docker configuration for backend
└── task-frontend/             # React frontend application
    ├── src/                   # TypeScript/React source code
    ├── public/                # Static assets
    ├── package.json           # NPM dependencies
    └── Dockerfile             # Docker configuration for frontend
```

## Prerequisites

Before you begin, ensure you have the following installed:

- **For Development**:
  - Java 17 or higher
  - Node.js 16 or higher
  - npm 8 or higher
  - Maven 3.8 or higher
  
- **For Deployment**:
  - Docker and Docker Compose (for containerized deployment)

## Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/reinaldo-globant/task-management-system.git
cd task-management-system
```

### 2. Backend Setup

```bash
cd task-backend

# Build the project
./mvnw clean install

# Run the backend server
./mvnw spring-boot:run
```

The backend API will be available at `http://localhost:8080/api`.

### 3. Frontend Setup

```bash
cd task-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend development server will be available at `http://localhost:5173`.

## Running with Docker

For a quick setup of the entire application stack:

```bash
# From the project root
docker-compose up
```

This will:
- Build and start the backend container (available at http://localhost:8080)
- Build and start the frontend container (available at http://localhost:80)
- Set up the necessary networking between containers

## API Documentation

The backend provides the following RESTful endpoints:

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate and receive JWT token

### Tasks

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/my-tasks` - Get current user's tasks
- `GET /api/tasks/{id}` - Get a specific task
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update an existing task
- `DELETE /api/tasks/{id}` - Delete a task

## Testing

### Backend Tests

```bash
cd task-backend
./mvnw test
```

### Frontend Tests

```bash
cd task-frontend

# Run unit tests
npm test

# Run E2E/feature tests
npm run test:e2e
```

## Feature Tests

The frontend includes BDD-style feature tests using Cucumber.js. These tests cover:

- User authentication flows
- Task creation and management
- Board interactions

See the `task-frontend/features` directory for the Gherkin feature definitions.

## Build for Production

### Backend

```bash
cd task-backend
./mvnw clean package
```

This creates a standalone JAR file in the `target` directory.

### Frontend

```bash
cd task-frontend
npm run build
```

This creates optimized production files in the `dist` directory.

## Deployment

### Option 1: Manual Deployment

1. Deploy the backend JAR file to your Java server
2. Deploy the frontend build files to your web server
3. Configure CORS and API endpoints as needed

### Option 2: Docker Deployment

```bash
docker-compose -f docker-compose.yml up -d
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[MIT License](LICENSE)

---

Built with ❤️ using Spring Boot, React, TypeScript, and Material UI.