Feature: User Authentication
  As a user
  I want to register and login to the system
  So that I can access my personal task board

  Background:
    Given the application is running
    And I am on the login page

  Scenario: User Registration
    When I click on the "Register here" link
    Then I should be redirected to the registration page
    When I fill in the following:
      | Full Name         | John Doe           |
      | Username          | johndoe            |
      | Email Address     | john.doe@email.com |
      | Password          | password123        |
      | Confirm Password  | password123        |
    And I click the "Register" button
    Then I should see a success message "Registration successful! Redirecting to login..."
    And I should be redirected to the login page

  Scenario: User Registration with mismatched passwords
    When I click on the "Register here" link
    Then I should be redirected to the registration page
    When I fill in the following:
      | Full Name         | John Doe           |
      | Username          | johndoe            |
      | Email Address     | john.doe@email.com |
      | Password          | password123        |
      | Confirm Password  | different456       |
    And I click the "Register" button
    Then I should see an error message "Passwords do not match"

  Scenario: Successful Login
    When I fill in the following:
      | Username | johndoe     |
      | Password | password123 |
    And I click the "Sign In" button
    Then I should be redirected to the task board page
    And I should see "Welcome, John Doe" in the header

  Scenario: Failed Login due to incorrect credentials
    When I fill in the following:
      | Username | johndoe     |
      | Password | wrong123    |
    And I click the "Sign In" button
    Then I should see an error message "Failed to login. Please check your credentials."
    And I should remain on the login page

  Scenario: Logout
    Given I am logged in as "johndoe"
    When I click the "Logout" button in the header
    Then I should be redirected to the login page
    And I should not see "Welcome, John Doe" in the header