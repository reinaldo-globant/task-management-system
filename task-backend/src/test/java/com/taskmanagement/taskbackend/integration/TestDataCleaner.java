package com.taskmanagement.taskbackend.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Utility class to clean up test data
 * Run with: mvn exec:java -Dexec.mainClass="com.taskmanagement.taskbackend.TestDataCleaner"
 */
@SpringBootApplication
@Profile("!production")
public class TestDataCleaner implements CommandLineRunner {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TestDataCleaner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (jdbcTemplate == null) {
            System.out.println("⚠️ Database not available, skipping cleanup");
            return;
        }

        System.out.println("🧹 Starting test data cleanup...");
        
        try {
            // Clean up test tasks (identified by test pattern)
            int deletedStatusChanges = jdbcTemplate.update(
                "DELETE FROM status_changes WHERE task_id IN (SELECT id FROM tasks WHERE title LIKE 'test-task-%')"
            );
            
            int deletedTasks = jdbcTemplate.update(
                "DELETE FROM tasks WHERE title LIKE 'test-task-%'"
            );
            
            System.out.println("✅ Cleaned up " + deletedStatusChanges + " status changes");
            System.out.println("✅ Cleaned up " + deletedTasks + " test tasks");
            
            // Note: We don't clean users from task-backend as they're managed by user-service
            
            System.out.println("🎉 Test data cleanup completed successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error during cleanup: " + e.getMessage());
            System.exit(1);
        }
        
        System.exit(0);
    }
}