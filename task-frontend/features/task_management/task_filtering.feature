Feature: Task Filtering and Listing
  As a logged-in user
  I want to view my tasks organized by status
  So that I can easily track my work progress

  Background:
    Given I am logged in as "johndoe"
    And I am on the task board page
    And the backend returns the following tasks for my user:
      | id | title                     | description                       | status      |
      | 1  | Implement login feature   | Create authentication UI and API  | TODO        |
      | 2  | Design database schema    | Create ERD and implement tables   | IN_PROGRESS |
      | 3  | Create user profile page  | Add user settings and avatar      | TODO        |
      | 4  | Setup test environment    | Configure Jest and testing tools  | IN_PROGRESS |
      | 5  | Deploy to production      | Setup CI/CD pipeline              | DONE        |
      | 6  | Write documentation       | Create user and developer docs    | DONE        |

  Scenario: Loading the task board displays tasks in the correct columns
    When the task board loads
    Then the API endpoint "/api/tasks/my-tasks" should receive a GET request
    And I should see 2 tasks in the "To Do" column
    And I should see 2 tasks in the "In Progress" column
    And I should see 2 tasks in the "Done" column
    And the task "Implement login feature" should be in the "To Do" column
    And the task "Design database schema" should be in the "In Progress" column
    And the task "Deploy to production" should be in the "Done" column

  Scenario: Task board updates when a task status changes
    Given the task board is loaded
    When I update the task "Implement login feature" to status "IN_PROGRESS"
    Then the API endpoint "/api/tasks/1" should receive a PUT request
    And I should see 1 task in the "To Do" column
    And I should see 3 tasks in the "In Progress" column
    And I should see 2 tasks in the "Done" column
    And the task "Implement login feature" should be in the "In Progress" column
    And the task "Implement login feature" should not be in the "To Do" column