-- Sample users
INSERT INTO users (username, email, password, full_name) VALUES 
('alice', 'alice@example.com', '$2a$10$8Nmo8uSLQJH.z3VWmhH4G.gzORXSx6JEr1YcMn9kEEwFEn6UQPffS', 'Alice Smith');

INSERT INTO users (username, email, password, full_name) VALUES 
('bob', 'bob@example.com', '$2a$10$8Nmo8uSLQJH.z3VWmhH4G.gzORXSx6JEr1YcMn9kEEwFEn6UQPffS', 'Bob Johnson');

INSERT INTO users (username, email, password, full_name) VALUES 
('charlie', 'charlie@example.com', '$2a$10$8Nmo8uSLQJH.z3VWmhH4G.gzORXSx6JEr1YcMn9kEEwFEn6UQPffS', 'Charlie Brown');

INSERT INTO users (username, email, password, full_name) VALUES 
('dave', 'dave@example.com', '$2a$10$8Nmo8uSLQJH.z3VWmhH4G.gzORXSx6JEr1YcMn9kEEwFEn6UQPffS', 'Dave Wilson');

-- Sample tasks (password for all users is 'password')
INSERT INTO tasks (title, description, status, owner_id, created_at) VALUES 
('Implement login feature', 'Create login screen and authentication logic', 'TODO', 1, CURRENT_TIMESTAMP());

INSERT INTO tasks (title, description, status, owner_id, created_at) VALUES 
('Design database schema', 'Create ERD and define relationships', 'IN_PROGRESS', 2, CURRENT_TIMESTAMP());

INSERT INTO tasks (title, description, status, owner_id, created_at) VALUES 
('Fix navigation bug', 'Address the issue with back button not working', 'DONE', 3, CURRENT_TIMESTAMP());

INSERT INTO tasks (title, description, status, owner_id, created_at) VALUES 
('Write user documentation', 'Create comprehensive user guide', 'TODO', 1, CURRENT_TIMESTAMP());

INSERT INTO tasks (title, description, status, owner_id, created_at) VALUES 
('Deploy to staging', 'Deploy current version to staging environment', 'IN_PROGRESS', 4, CURRENT_TIMESTAMP());