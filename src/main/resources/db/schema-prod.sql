-- ============================================
-- PRODUCTION SCHEMA SCRIPT
-- ============================================
-- Environment: Production (spring.profiles.active=prod)
-- Mode: Manual execution only (spring.sql.init.mode=never)
-- Behavior: Creates tables ONLY if they don't exist
-- Safety: Idempotent - safe to run multiple times, no data loss
-- ============================================
--
-- USAGE:
-- Run manually once in production:
--   mysql -u dbexp -p db_experiment < src/main/resources/db/schema-prod.sql
--
-- RECOMMENDATION:
-- For production environments, consider using a proper migration tool
-- like Flyway or Liquibase for version-controlled schema changes.
-- ============================================

USE db_experiment;

-- ============================================
-- CREATE CORE TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS ForumUser (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Community (
    community_id INT PRIMARY KEY AUTO_INCREMENT,
    community_name VARCHAR(255) NOT NULL UNIQUE,
    community_title TEXT DEFAULT NULL,
    community_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- CREATE CONTENT TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS Post (
    post_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    community_id INT NOT NULL,
    post_title VARCHAR(255) NOT NULL,
    post_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES Community(community_id)
);

-- Create indexes only if they don't exist (MySQL 5.7+)
CREATE INDEX IF NOT EXISTS idx_post_user_id ON Post(user_id);
CREATE INDEX IF NOT EXISTS idx_post_community_id ON Post(community_id);

CREATE TABLE IF NOT EXISTS ForumComment (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    comment_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comment_user_id ON ForumComment(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_post_id ON ForumComment(post_id);

-- ============================================
-- CREATE RELATIONSHIP TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS Subscription (
    user_id INT NOT NULL,
    community_id INT NOT NULL,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, community_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES Community(community_id)
);

CREATE TABLE IF NOT EXISTS CommunityModerator (
    community_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (community_id, user_id),
    FOREIGN KEY (community_id) REFERENCES Community(community_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE
);

-- ============================================
-- CREATE VOTING TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS PostVote (
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_postvote_post_id ON PostVote(post_id);

CREATE TABLE IF NOT EXISTS CommentVote (
    user_id INT NOT NULL,
    comment_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, comment_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES ForumComment(comment_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_commentvote_comment_id ON CommentVote(comment_id);

-- ============================================
-- VERIFICATION
-- ============================================
-- Uncomment to verify table creation:
-- SHOW TABLES;
-- SELECT COUNT(*) AS table_count FROM information_schema.tables
-- WHERE table_schema = 'db_experiment';