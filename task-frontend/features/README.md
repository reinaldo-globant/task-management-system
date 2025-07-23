# Task Management Application Tests

This directory contains Behavior-Driven Development (BDD) test specifications written in Gherkin format. These specifications describe the expected behavior of the task management application from an end-user perspective.

## Test Structure

The tests are organized by feature area:

- **Authentication**: Tests for user registration, login, and logout functionality
- **Task Management**: Tests for creating, updating, deleting, and viewing tasks

## Running the Tests

To run these tests, you would need to implement step definitions that map the Gherkin statements to actual test code. This would typically involve:

1. Installing Cucumber.js and related testing libraries:

```bash
npm install --save-dev @cucumber/cucumber @cucumber/pretty-formatter
npm install --save-dev jest @types/jest
npm install --save-dev @testing-library/react @testing-library/jest-dom @testing-library/user-event
```

2. Setting up step definitions in a `step_definitions` directory that implements each of the steps in the feature files

3. Configuring Cucumber to run with your React application, possibly using tools like `@cucumber/react` for integration

## Test Coverage

These Gherkin specifications cover:

### Authentication
- User registration with validation
- Login with correct and incorrect credentials
- User logout functionality

### Task Management
- Creating new tasks
- Updating existing tasks
- Deleting tasks
- Viewing tasks filtered by status

## API Endpoints Tested

These tests verify that the frontend correctly communicates with the following backend endpoints:

- `POST /api/auth/signup` - User registration
- `POST /api/auth/signin` - User authentication
- `GET /api/tasks/my-tasks` - Get all tasks for the current user
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update an existing task
- `DELETE /api/tasks/{id}` - Delete a task

## Example Step Definition Implementation

```javascript
import { Given, When, Then } from '@cucumber/cucumber';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from '../../src/App';

Given('I am on the login page', () => {
  render(<App />);
  expect(screen.getByText('Login')).toBeInTheDocument();
});

When('I fill in the following:', (dataTable) => {
  const formData = dataTable.rowsHash();
  
  for (const [field, value] of Object.entries(formData)) {
    const input = screen.getByLabelText(field);
    userEvent.type(input, value);
  }
});

When('I click the {string} button', (buttonText) => {
  const button = screen.getByText(buttonText);
  userEvent.click(button);
});

Then('I should be redirected to the task board page', async () => {
  await waitFor(() => {
    expect(screen.getByText('My Task Board')).toBeInTheDocument();
  });
});
```