-- ============================================
-- SAMPLE TEST DATA (Development Only)
-- ============================================
-- Environment: Development (spring.profiles.active=dev)
-- Loaded automatically after schema.sql when mode=always
-- Purpose: Provide realistic test data for development and testing
-- ============================================

USE db_experiment;

-- ============================================
-- Sample Users
-- ============================================
-- Note: In production, use proper password hashing (BCrypt, etc.)
INSERT INTO ForumUser (username, password_hash, email) VALUES
('alice', '$2a$10$dummyhash1', 'alice@example.com'),
('bob', '$2a$10$dummyhash2', 'bob@example.com'),
('charlie', '$2a$10$dummyhash3', 'charlie@example.com'),
('diana', '$2a$10$dummyhash4', 'diana@example.com'),
('eve', '$2a$10$dummyhash5', 'eve@example.com');

-- ============================================
-- Sample Communities
-- ============================================
INSERT INTO Community (community_name, community_title, community_description) VALUES
('cscareerquestions', 'CS Career Questions', 'CSCareerQuestions is a community for those interested in computer science careers, job searching, interviews, and career development.'),
('gamingcirclejerk', 'GamingCirclejerk', 'Welcome home, friend.'),
('programmerhumor', 'Programmer Humor', 'For anything funny related to programming and software development.'),
('science', 'Science & Research', 'Latest discoveries and scientific discussions'),
('music', 'Music Lovers', 'Share and discuss all genres of music'),
('asoiaf', 'A Song of Ice and Fire', 'News and discussions relating to George R.R. Martin''s "A Song of Ice and Fire" novels.');

-- ============================================
-- Sample Posts
-- ============================================
INSERT INTO Post (user_id, community_id, post_title, post_content) VALUES
-- cscareerquestions posts
(1, 1, 'Just got laid off AMA', '3 YOE, thought I was safe. Joke''s on me!'),
(3, 1, 'AI taking our jobs yet?', 'Should I switch careers before Skynet activates?'),
(5, 1, 'Real talk: Is Java still worth learning?', 'Asking for a friend who''s stuck in 2010'),

-- gamingcirclejerk posts
(2, 2, 'What game are we all pretending to hate this week?', 'I''ll start: Starfield'),
(4, 2, 'Console peasants vs PC master race', 'Fight me IRL'),

-- programmerhumor posts
(1, 3, 'Java vs Python: Which one sucks less?', 'Daily reminder that both have awful packaging systems'),
(3, 3, 'Unpopular opinion: IDEs are crutches', 'Real programmers use butterflies'),
(5, 3, 'Spring Boot: Why does everything break on update?', 'Asking for 300 colleagues'),

-- science posts
(4, 4, 'Climate doomposting thread', 'We''re so fucked edition'),
(2, 4, 'SpaceX just did WHAT?', 'Elon pls stop'),

-- music posts
(5, 5, 'Unpopular music opinions thread', 'I''ll start: Beatles are overrated'),

-- asoiaf posts
(1, 6, 'Latest Not A Blog Post', 'GRRM confirms that TWOW will be released before 2075.');

-- ============================================
-- Sample Comments
-- ============================================
INSERT INTO ForumComment (user_id, post_id, comment_content) VALUES
-- Comments on cscareerquestions posts
(2, 1, 'RIP OP. Thoughts and prayers'),
(3, 1, 'First!'),
(4, 2, 'Jokes on you, Skynet runs on Java'),
(5, 2, 'GPT-10 wrote this comment'),
(2, 3, 'Java? In this economy?'),
(4, 3, 'Real ones use COBOL'),

-- Comments on gamingcirclejerk posts
(1, 4, 'BG3 is mid. Fight me.'),
(3, 4, 'Elden Ring? More like Elden Copium'),
(5, 5, 'Not yielding to corporate greed.'),
(1, 5, 'Consoles are for peasants'),

-- Comments on programmerhumor posts
(2, 6, 'Python: because we hate our future selves'),
(4, 6, 'Java: because your enterprise loves boilerplate'),
(5, 7, 'IntelliJ gang rise up!'),
(1, 7, 'OK which of you pensioner is still using Java in 2025?'),

-- Comments on science posts
(1, 9, 'We''re so fucked lmao'),
(3, 10, 'Pics or it didn''t happen'),

-- Comments on music posts
(2, 11, 'Indie rock? Name three songs.'),

-- Comments on asoiaf posts
(3, 12, 'Poor Jon Snow trapped in ADWD for 50+ years.');

-- ============================================
-- Sample Subscriptions
-- ============================================
INSERT INTO Subscription (user_id, community_id) VALUES
-- Alice subscriptions
(1, 1), (1, 3), (1, 6),
-- Bob subscriptions
(2, 2), (2, 3), (2, 4),
-- Charlie subscriptions
(3, 1), (3, 2), (3, 4), (3, 5),
-- Diana subscriptions
(4, 1), (4, 4), (4, 5),
-- Eve subscriptions
(5, 1), (5, 3), (5, 5), (5, 6);

-- ============================================
-- Sample Moderators
-- ============================================
INSERT INTO CommunityModerator (community_id, user_id) VALUES
(1, 1),  -- alice moderates technology
(2, 2),  -- bob moderates gaming
(3, 1),  -- alice also moderates programming
(3, 5),  -- eve co-moderates programming
(4, 4),  -- diana moderates science
(5, 5),  -- eve moderates music
(6, 1);  -- alice moderates books

-- ============================================
-- Sample Post Votes
-- ============================================
INSERT INTO PostVote (user_id, post_id, vote_value) VALUES
-- Upvotes
(2, 1, 1), (3, 1, 1), (4, 1, 1), (5, 1, 1),
(1, 2, 1), (2, 2, 1), (5, 2, 1),
(1, 3, 1), (2, 3, 1),
(1, 4, 1), (3, 4, 1), (5, 4, 1),
(1, 5, 1), (2, 5, 1), (3, 5, 1),
(2, 6, 1), (3, 6, 1), (5, 6, 1),
(1, 7, 1), (2, 7, 1),
(1, 8, 1), (2, 8, 1), (3, 8, 1),
(1, 9, 1), (3, 9, 1), (5, 9, 1),
-- Some downvotes for realism
(4, 3, -1),
(4, 5, -1),
(4, 6, -1),
(4, 7, -1);

-- ============================================
-- Sample Comment Votes
-- ============================================
INSERT INTO CommentVote (user_id, comment_id, vote_value) VALUES
-- Upvotes on comments
(1, 1, 1), (3, 1, 1), (4, 1, 1),
(1, 2, 1), (2, 2, 1),
(1, 4, 1), (2, 4, 1), (3, 4, 1),
(1, 5, 1), (3, 5, 1),
(1, 6, 1), (3, 6, 1), (5, 6, 1),
(2, 7, 1), (3, 7, 1), (4, 7, 1),
(2, 9, 1), (4, 9, 1),
(3, 11, 1), (5, 11, 1),
-- Some downvotes
(5, 10, -1),
(4, 12, -1);

-- ============================================
-- Data Loading Complete
-- ============================================
-- Tables populated:
-- - 5 Users
-- - 6 Communities
-- - 12 Posts
-- - 20+ Comments
-- - Multiple subscriptions, moderators, and votes
-- ============================================