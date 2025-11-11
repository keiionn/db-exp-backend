-- This script creates the table structure for a community platform like Reddit or a forum.
-- It is written in a standard SQL dialect and should be compatible with most RDBMS like PostgreSQL, MySQL, and SQLite.

-- =============================================
-- Core Entities: ForumUsers and Communities
-- =============================================

CREATE DATABASE IF NOT EXISTS db_experiment;

CREATE USER IF NOT EXISTS 'dbexp'@'localhost' IDENTIFIED BY 'A1b212345';
GRANT ALL PRIVILEGES ON db_experiment.* TO 'dbexp'@'localhost';
FLUSH PRIVILEGES;

USE db_experiment;

CREATE TABLE IF NOT EXISTS ForumUser (
    user_id INT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Community (
    community_id INT PRIMARY KEY,
    community_name VARCHAR(255) NOT NULL UNIQUE,
    community_title TEXT DEFAULT NULL,
    community_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); -- Community CANNOT be deleted

-- =============================================
-- Content Entities: Posts and Comments
-- =============================================

CREATE TABLE IF NOT EXISTS Post (
    post_id INT PRIMARY KEY,
    user_id INT NOT NULL,
    community_id INT NOT NULL,
    post_title VARCHAR(255) NOT NULL,
    post_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES Community(community_id)
);

CREATE INDEX idx_post_user_id ON Post(user_id);
CREATE INDEX idx_post_community_id ON Post(community_id);

CREATE TABLE IF NOT EXISTS ForumComment (
    comment_id INT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    comment_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE
);

-- =============================================
-- Relationship / Junction Tables
-- =============================================

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

CREATE INDEX idx_comment_user_id ON ForumComment(user_id);
CREATE INDEX idx_comment_post_id ON ForumComment(post_id);

-- =============================================
-- Voting Tables
-- =============================================

CREATE TABLE IF NOT EXISTS PostVote (
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS CommentVote (
    user_id INT NOT NULL,
    comment_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, comment_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES ForumComment(comment_id) ON DELETE CASCADE
);

CREATE INDEX idx_postvote_post_id ON PostVote(post_id);
CREATE INDEX idx_commentvote_comment_id ON CommentVote(comment_id);