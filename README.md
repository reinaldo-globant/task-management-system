# Task Management System

A full-stack task management application with a microservices architecture.

## Project Overview

This task management system provides a modern, responsive interface for organizing tasks across different status columns (To Do, In Progress, Done). It features:

- **User Authentication**: Secure login and registration system using JWT tokens
- **Single Sign-On (SSO)**: OAuth2 integration with Google, GitHub, and Microsoft
- **Task Board**: Visual Kanban-style board with status columns
- **Task Management**: Create, read, update, and delete tasks
- **Side Modal**: Detailed task view and editing through a slide-in panel
- **Responsive Design**: Works on desktop and mobile devices

## Microservices Architecture

This repository contains the following services:

```
task-management-system/
├── docker-compose.yml         # Docker configuration for full-stack deployment
├── user-service/              # Spring Boot User Authentication Service
│   ├── src/                   # Java source code
│   ├── pom.xml                # Maven dependencies
│   └── Dockerfile             # Docker configuration for user service
├── task-backend/              # Spring Boot Task Management Service
│   ├── src/                   # Java source code
│   ├── pom.xml                # Maven dependencies
│   └── Dockerfile             # Docker configuration for task service
└── task-frontend/             # React frontend application
    ├── src/                   # TypeScript/React source code
    ├── public/                # Static assets
    ├── package.json           # NPM dependencies
    └── Dockerfile             # Docker configuration for frontend
```

### Architecture Diagram

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│             │      │             │      │             │
│  Frontend   │◄────►│Task Service │◄────►│User Service │
│  (React)    │      │(Spring Boot)│      │(Spring Boot)│
│             │      │             │      │             │
└─────────────┘      └─────────────┘      └─────────────┘
       │                    │                    │
       │                    │                    │
       ▼                    ▼                    ▼
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│             │      │             │      │             │
│   Nginx     │      │ PostgreSQL  │      │ PostgreSQL  │
│  Container  │      │  (Tasks)    │      │  (Users)    │
│             │      │             │      │             │
└─────────────┘      └─────────────┘      └─────────────┘
```

## Prerequisites

Before you begin, ensure you have the following installed:

- **For Development**:
  - Java 17 or higher
  - Node.js 16 or higher
  - npm 8 or higher
  - Maven 3.8 or higher
  - PostgreSQL 14 or higher
  
- **For Deployment**:
  - Docker and Docker Compose (for containerized deployment)

## Running with Docker

For a quick setup of the entire application stack:

```bash
# From the project root
docker-compose up
```

This will:
- Start PostgreSQL databases for both user and task services
- Build and start the user service (available at http://localhost:8081)
- Build and start the task service (available at http://localhost:8080)
- Build and start the frontend container (available at http://localhost:80)
- Set up the necessary networking between containers

## Manual Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/reinaldo-globant/task-management-system.git
cd task-management-system
```

### 2. Database Setup

```bash
# Start PostgreSQL for User Service
docker run -d --name user-db -e POSTGRES_DB=userdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:14

# Start PostgreSQL for Task Service
docker run -d --name task-db -e POSTGRES_DB=taskdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5433:5432 postgres:14
```

### 3. User Service Setup

```bash
cd user-service

# Build the project
./mvnw clean install

# Run the user service
./mvnw spring-boot:run
```

The user service will be available at `http://localhost:8081`.

### 4. Task Service Setup

```bash
cd task-backend

# Build the project
./mvnw clean install

# Run the task service
./mvnw spring-boot:run
```

The task service will be available at `http://localhost:8080`.

### 5. Frontend Setup

```bash
cd task-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend development server will be available at `http://localhost:5173`.

## Single Sign-On (SSO) Setup

This application includes OAuth2 integration for seamless authentication with popular providers:

- **Google OAuth2** - Sign in with Google accounts
- **GitHub OAuth2** - Sign in with GitHub accounts  
- **Microsoft OAuth2** - Sign in with Microsoft/Azure AD accounts

### Quick Start (No Configuration Required)

The SSO integration is **ready to use** out of the box:

1. Start the application: `docker-compose up`
2. Navigate to: `http://localhost:5173/login`
3. You'll see OAuth2 section with status message
4. **For full OAuth2 functionality**: Follow the [SSO Setup Guide](SSO_SETUP.md)

### Features

- ✅ **Dynamic Provider Detection** - Only shows configured providers
- ✅ **Account Linking** - Links OAuth2 accounts via email matching
- ✅ **JWT Integration** - OAuth2 users get same JWT tokens as regular users
- ✅ **Security** - Follows OAuth2 best practices and CORS configuration
- ✅ **Production Ready** - Includes environment configuration and deployment guides

For detailed OAuth2 configuration instructions, see [SSO_SETUP.md](SSO_SETUP.md).

## API Documentation

### User Service API

#### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate and receive JWT token
- `POST /api/users/validate` - Validate a JWT token

#### OAuth2 SSO
- `GET /api/oauth2/providers` - Get available OAuth2 providers
- `GET /oauth2/authorization/{provider}` - Initiate OAuth2 login (Google, GitHub, Microsoft)
- `GET /oauth2/redirect` - Handle OAuth2 callback

#### User Management
- `GET /api/users` - Get all users (admin only)
- `GET /api/users/{id}` - Get user by ID

### Task Service API

- `GET /api/tasks` - Get all tasks (admin only)
- `GET /api/tasks/my-tasks` - Get current user's tasks
- `GET /api/tasks/{id}` - Get a specific task
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update an existing task
- `DELETE /api/tasks/{id}` - Delete a task

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[MIT License](LICENSE)

---

Built with ❤️ using Spring Boot, React, TypeScript, and Material UI.