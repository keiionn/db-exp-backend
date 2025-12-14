-- ============================================
-- DEVELOPMENT SCHEMA SCRIPT
-- ============================================
-- Environment: Development (spring.profiles.active=dev)
-- Mode: spring.sql.init.mode=always
-- Behavior: DROPS and RECREATES all tables on EVERY startup
-- WARNING: ALL DATA IS LOST on every application restart!
-- ============================================

USE db_experiment;

-- ============================================
-- DROP TABLES (Reverse Dependency Order)
-- ============================================

DROP TABLE IF EXISTS CommentVote;
DROP TABLE IF EXISTS PostVote;
DROP TABLE IF EXISTS CommunityModerator;
DROP TABLE IF EXISTS Subscription;
DROP TABLE IF EXISTS ForumComment;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS Community;
DROP TABLE IF EXISTS ForumUser;

-- ============================================
-- CREATE CORE TABLES
-- ============================================

CREATE TABLE ForumUser (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Community (
    community_id INT PRIMARY KEY AUTO_INCREMENT,
    community_name VARCHAR(255) NOT NULL UNIQUE,
    community_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- CREATE CONTENT TABLES
-- ============================================

CREATE TABLE Post (
    post_id INT PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE ForumComment (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    parent_comment_id INT,
    comment_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES ForumComment(comment_id) ON DELETE CASCADE
);

CREATE INDEX idx_comment_user_id ON ForumComment(user_id);
CREATE INDEX idx_comment_post_id ON ForumComment(post_id);

-- ============================================
-- CREATE RELATIONSHIP TABLES
-- ============================================

CREATE TABLE Subscription (
    user_id INT NOT NULL,
    community_id INT NOT NULL,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, community_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (community_id) REFERENCES Community(community_id)
);

CREATE TABLE CommunityModerator (
    community_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (community_id, user_id),
    FOREIGN KEY (community_id) REFERENCES Community(community_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE
);

-- ============================================
-- CREATE VOTING TABLES
-- ============================================

CREATE TABLE PostVote (
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(post_id) ON DELETE CASCADE
);

CREATE INDEX idx_postvote_post_id ON PostVote(post_id);

CREATE TABLE CommentVote (
    user_id INT NOT NULL,
    comment_id INT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (-1, 1)),
    PRIMARY KEY (user_id, comment_id),
    FOREIGN KEY (user_id) REFERENCES ForumUser(user_id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES ForumComment(comment_id) ON DELETE CASCADE
);

CREATE INDEX idx_commentvote_comment_id ON CommentVote(comment_id);