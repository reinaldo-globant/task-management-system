# Task Manager Frontend

A React-based frontend for the Task Management application that allows users to manage tasks across different statuses.

## Features

- User authentication (login/register)
- Task board with drag-and-drop interface
- Create, edit, and delete tasks
- Filter tasks by status (To Do, In Progress, Done)
- Responsive design for various screen sizes

## Technologies Used

- React with TypeScript
- React Router for navigation
- Material UI for components and styling
- Axios for API communication
- JWT authentication
- Cucumber.js for BDD testing

## Prerequisites

- Node.js 14.x or higher
- npm or yarn
- Backend API running on http://localhost:8080

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Install dependencies:

```bash
npm install
```

4. Start the development server:

```bash
npm run dev
```

5. Open your browser and visit http://localhost:5173

## Project Structure

```
src/
├── components/       # Reusable UI components
├── contexts/         # React contexts for state management
├── pages/            # Page components
├── services/         # API service functions
├── types/            # TypeScript interfaces
├── App.tsx           # Main application component
└── main.tsx          # Application entry point

features/
├── authentication/                 # Authentication feature specifications
├── task_management/                # Task management feature specifications
└── step_definitions/               # Test implementation for feature steps
    ├── authentication/             # Authentication test implementations
    └── task_management/            # Task management test implementations
```

## Communication with Backend

The frontend communicates with the Spring Boot backend using RESTful API endpoints:

- Authentication: `/api/auth/*`
- Task Management: `/api/tasks/*`

See the backend README for detailed API documentation.

## Testing

The application includes Behavior-Driven Development (BDD) tests written in Gherkin format. These tests describe the expected behavior of the application from a user's perspective.

To run the tests, first install the required dependencies:

```bash
npm install --save-dev @cucumber/cucumber @cucumber/pretty-formatter jest @testing-library/react @testing-library/jest-dom @testing-library/user-event
```

Then run the tests with:

```bash
npm test
```

The test suite covers:

- Authentication (register, login, logout)
- Task creation, updating, and deletion
- Task filtering and display by status

## Building for Production

To build the application for production, run:

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.