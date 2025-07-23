Feature: Task Creation
  As a logged-in user
  I want to create new tasks
  So that I can manage my work items

  Background:
    Given I am logged in as "johndoe"
    And I am on the task board page

  Scenario: Create a new task with minimal information
    When I click on the "New Task" button
    Then a task creation dialog should appear
    When I fill in the following:
      | Title | Implement login feature |
    And I click the "Create" button
    Then the dialog should close
    And I should see a task with title "Implement login feature" in the "To Do" column
    And the API endpoint "/api/tasks" should receive a POST request with:
      """
      {
        "title": "Implement login feature",
        "description": "",
        "status": "TODO"
      }
      """

  Scenario: Create a new task with full information
    When I click on the "New Task" button
    Then a task creation dialog should appear
    When I fill in the following:
      | Title       | Implement login feature                               |
      | Description | Create UI and backend for user authentication process |
    And I select "In Progress" from the "Status" dropdown
    And I click the "Create" button
    Then the dialog should close
    And I should see a task with title "Implement login feature" in the "In Progress" column
    And the API endpoint "/api/tasks" should receive a POST request with:
      """
      {
        "title": "Implement login feature",
        "description": "Create UI and backend for user authentication process",
        "status": "IN_PROGRESS"
      }
      """

  Scenario: Attempt to create a task without a title
    When I click on the "New Task" button
    Then a task creation dialog should appear
    When I leave the "Title" field empty
    And I click the "Create" button
    Then I should see an error message "Title is required"
    And the dialog should remain open
    And no request should be sent to the "/api/tasks" endpoint