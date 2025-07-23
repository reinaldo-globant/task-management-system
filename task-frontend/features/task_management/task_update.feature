Feature: Task Update
  As a logged-in user
  I want to update my existing tasks
  So that I can track their progress and details

  Background:
    Given I am logged in as "johndoe"
    And I am on the task board page
    And I have the following tasks:
      | id | title                  | description                       | status      |
      | 1  | Implement login feature| Create authentication UI and API  | TODO        |
      | 2  | Design database schema | Create ERD and implement tables   | IN_PROGRESS |
      | 3  | Deploy to production   | Setup CI/CD pipeline              | DONE        |

  Scenario: Update task title and description
    When I click on the task "Implement login feature"
    Then a task edit dialog should appear with the task details
    When I update the following:
      | Title       | Implement user authentication      |
      | Description | Create UI forms and API endpoints  |
    And I click the "Save" button
    Then the dialog should close
    And I should see a task with title "Implement user authentication" in the "To Do" column
    And the API endpoint "/api/tasks/1" should receive a PUT request with:
      """
      {
        "title": "Implement user authentication",
        "description": "Create UI forms and API endpoints",
        "status": "TODO"
      }
      """

  Scenario: Update task status
    When I click on the task "Implement login feature"
    Then a task edit dialog should appear with the task details
    When I select "In Progress" from the "Status" dropdown
    And I click the "Save" button
    Then the dialog should close
    And I should see a task with title "Implement login feature" in the "In Progress" column
    And the task should no longer appear in the "To Do" column
    And the API endpoint "/api/tasks/1" should receive a PUT request with:
      """
      {
        "title": "Implement login feature",
        "description": "Create authentication UI and API",
        "status": "IN_PROGRESS"
      }
      """

  Scenario: Attempt to update a task without a title
    When I click on the task "Implement login feature"
    Then a task edit dialog should appear with the task details
    When I clear the "Title" field
    And I click the "Save" button
    Then I should see an error message "Title is required"
    And the dialog should remain open
    And no request should be sent to the "/api/tasks/1" endpoint