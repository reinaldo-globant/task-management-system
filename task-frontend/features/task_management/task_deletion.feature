Feature: Task Deletion
  As a logged-in user
  I want to delete tasks I no longer need
  So that I can keep my task board organized

  Background:
    Given I am logged in as "johndoe"
    And I am on the task board page
    And I have the following tasks:
      | id | title                  | description                       | status      |
      | 1  | Implement login feature| Create authentication UI and API  | TODO        |
      | 2  | Design database schema | Create ERD and implement tables   | IN_PROGRESS |
      | 3  | Deploy to production   | Setup CI/CD pipeline              | DONE        |

  Scenario: Delete a task
    When I click on the task "Implement login feature"
    Then a task edit dialog should appear with the task details
    When I click the delete button in the bottom right corner
    Then a confirmation dialog should appear with the message "Are you sure you want to delete this task? This action cannot be undone."
    When I click the "Delete" button on the confirmation dialog
    Then the confirmation dialog should close
    And the task edit dialog should close
    And I should no longer see the task "Implement login feature" in the "To Do" column
    And the API endpoint "/api/tasks/1" should receive a DELETE request

  Scenario: Cancel task deletion
    When I click on the task "Implement login feature"
    Then a task edit dialog should appear with the task details
    When I click the delete button in the bottom right corner
    Then a confirmation dialog should appear with the message "Are you sure you want to delete this task? This action cannot be undone."
    When I click the "Cancel" button on the confirmation dialog
    Then the confirmation dialog should close
    And the task edit dialog should remain open
    And no request should be sent to the "/api/tasks/1" endpoint