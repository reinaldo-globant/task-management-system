# Task Management Frontend

This is the frontend component of the Task Management System, built with React, TypeScript, and Material UI.

## Features

- **Authentication System**: Login and registration with JWT token management
- **Task Board**: Kanban-style board with status columns (To Do, In Progress, Done)
- **Task Management**: Create, view, edit, and delete tasks
- **Side Modal**: Detailed task information in a right-side drawer with overlay
- **Responsive Design**: Works on desktop and mobile devices

## Tech Stack

- **React 18**: UI library
- **TypeScript**: Type-safe JavaScript
- **Vite**: Fast development environment and build tool
- **Material UI**: Component library for consistent design
- **React Router**: Navigation and routing
- **Axios**: API requests
- **JWT Authentication**: Secure user sessions
- **Cucumber.js**: BDD-style testing

## Project Structure

```
src/
├── components/             # Reusable UI components
│   ├── Header.tsx          # App header with navigation
│   ├── ProtectedRoute.tsx  # Route that requires authentication
│   ├── TaskCard.tsx        # Individual task display
│   ├── TaskColumn.tsx      # Column for organizing tasks by status
│   └── TaskSideModal.tsx   # Side drawer for detailed task view/edit
├── contexts/
│   └── AuthContext.tsx     # Authentication state management
├── pages/
│   ├── LoginPage.tsx       # User login
│   ├── RegisterPage.tsx    # User registration 
│   └── TaskBoardPage.tsx   # Main task board
├── services/
│   ├── authService.ts      # Authentication API calls
│   └── taskService.ts      # Task management API calls
├── types/
│   └── index.ts            # TypeScript type definitions
├── App.tsx                 # Main application component
└── main.tsx               # Application entry point
```

## Getting Started

### Prerequisites

- Node.js 16 or higher
- npm 8 or higher

### Installation

```bash
# Install dependencies
npm install
```

### Development

```bash
# Start development server
npm run dev
```

The application will be available at `http://localhost:5173`.

### Environment Variables

Create a `.env` file in the project root with the following variables:

```
VITE_API_URL=http://localhost:8080/api
```

For Docker deployment, there's a separate `.env.docker` file which is used automatically.

## Build

```bash
# Type check
npm run typecheck

# Production build
npm run build
```

The build artifacts will be in the `dist` directory.

## Testing

The frontend includes both unit tests and feature tests using Cucumber.js for BDD-style testing.

```bash
# Run unit tests
npm test

# Run feature tests
npm run test:e2e
```

### Feature Tests

Feature tests are located in the `features` directory and describe user scenarios in Gherkin syntax. They cover:

- User authentication (login, register)
- Task management (create, update, delete tasks)
- Task board interactions

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Create production build
- `npm run typecheck` - Run TypeScript type checking
- `npm test` - Run unit tests
- `npm run test:e2e` - Run feature tests
- `npm run lint` - Run linting
- `npm run preview` - Preview production build locally

## Docker

The frontend can be built and run in a Docker container:

```bash
# Build the image
docker build -t task-frontend .

# Run the container
docker run -p 80:80 task-frontend
```

## Design Decisions

- **JWT Authentication**: Stored in localStorage for persistent sessions
- **Material UI**: Used for consistent design and responsive components
- **Context API**: Used for state management instead of Redux for simplicity
- **TypeScript**: Used throughout for type safety and better developer experience
- **Side Modal**: Implemented as a drawer component for better mobile UX