# Task Management System

A full-stack task management application with a Spring Boot backend and React frontend.

## Project Structure

This project consists of two main components:

- **task-backend**: A Spring Boot application providing the REST API for task management
- **task-frontend**: A React application with TypeScript and Material UI for the user interface

## Features

- User authentication with JWT
- Task management (Create, Read, Update, Delete)
- Task board with status columns (To Do, In Progress, Done)
- Responsive design
- Side modal for detailed task information

## Prerequisites

- Java 17+
- Node.js 16+
- npm 8+
- Docker (optional, for containerization)

## Running the Application

### Using Docker Compose

The easiest way to run the full application stack is using Docker Compose:

```bash
docker-compose up
```

This will start both the backend and frontend services.

### Running Frontend Locally

```bash
cd task-frontend
npm install
npm run dev
```

The frontend development server will start at http://localhost:5173.

### Running Backend Locally

```bash
cd task-backend
./mvnw spring-boot:run
```

The backend API will be available at http://localhost:8080/api.

## Testing

The frontend includes both unit tests and feature tests using Cucumber.js for BDD-style testing.

```bash
cd task-frontend
npm test             # Run unit tests
npm run test:e2e     # Run feature tests
```

## License

[MIT License](LICENSE)