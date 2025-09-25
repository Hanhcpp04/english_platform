CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NULL,
    fullname VARCHAR(255) NOT NULL,
    avatar VARCHAR(500) NULL,
    role ENUM('ADMIN','USER') DEFAULT 'USER',
    google_id VARCHAR(255) NULL,
    facebook_id VARCHAR(255) NULL,
    total_xp INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_total_xp (total_xp),
    INDEX idx_role (role)
);

CREATE TABLE level (
    level_number INT PRIMARY KEY,
    level_name VARCHAR(100) NOT NULL,
    min_xp INT NOT NULL,
    max_xp INT NULL,
    description TEXT NULL,
    INDEX idx_xp_range (min_xp, max_xp)
);

CREATE TABLE badges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    icon_url VARCHAR(500) NULL,
    condition_type ENUM(
        'vocabulary',
        'grammar',
        'listening',
        'reading',
        'writing',
        'testing',
        'forum',
        'streak',
        'accuracy') NOT NULL,
    condition_value INT NOT NULL,
    xp_reward INT DEFAULT 0 CHECK (xp_reward >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_condition (condition_type, condition_value),
    INDEX idx_active (is_active)
);

CREATE TABLE user_badges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    badge_id INT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_badge (user_id, badge_id),
    INDEX idx_user_earned (user_id, earned_at),
    INDEX idx_badge_earned (badge_id, earned_at)
);

CREATE TABLE vocab_topics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    icon_url VARCHAR(500) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    xp_reward INT DEFAULT 100,
    total_words INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vocab_words (
    id INT PRIMARY KEY AUTO_INCREMENT,
    topic_id INT NOT NULL,
    english_word VARCHAR(255) NOT NULL,
    vietnamese_meaning TEXT NOT NULL,
    pronunciation VARCHAR(255) NULL,
    audio_url VARCHAR(500) NULL,
    image_url VARCHAR(500) NULL,
    example_sentence TEXT NULL,
    example_translation TEXT NULL,
    xp_reward INT DEFAULT 3 CHECK (xp_reward >= 0),
    word_type VARCHAR(100) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES vocab_topics(id) ON DELETE CASCADE,
    INDEX idx_topic_active (topic_id, is_active),
    INDEX idx_english_word (english_word),
    INDEX idx_word_type (word_type)
);

CREATE TABLE vocab_exercise_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    topic_id INT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES vocab_topics(id) ON DELETE CASCADE,
    UNIQUE KEY unique_topic_exercise_type (topic_id, name),
    INDEX idx_topic_active (topic_id, is_active)
);

CREATE TABLE vocab_exercise_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    type_id INT NOT NULL,
    word_id INT NULL,
    question TEXT NOT NULL,
    options JSON NULL,
    correct_answer TEXT NOT NULL,
    xp_reward INT DEFAULT 5,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES vocab_exercise_type(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES vocab_words(id) ON DELETE CASCADE,
    INDEX idx_type_active (type_id, is_active),
    INDEX idx_word (word_id)
);

CREATE TABLE vocab_user_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    word_id INT NOT NULL,
    topic_id INT NOT NULL,
    question_id INT NULL,
    type ENUM('flashcard','exercise') NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (word_id) REFERENCES vocab_words(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES vocab_topics(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES vocab_exercise_questions(id) ON DELETE SET NULL,
    UNIQUE KEY unique_user_word_question_type (user_id, word_id, question_id, type),
    INDEX idx_user_topic (user_id, topic_id),
    INDEX idx_completed (is_completed, completed_at)
);

CREATE TABLE grammar_topics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    xp_reward INT DEFAULT 100,
    INDEX idx_active (is_active)
);

CREATE TABLE grammar_lessons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    topic_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    xp_reward INT DEFAULT 100,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES grammar_topics(id) ON DELETE CASCADE,
    INDEX idx_topic_active (topic_id, is_active)
);

CREATE TABLE exercise_grammar_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    topic_id INT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES grammar_topics(id) ON DELETE CASCADE,
    UNIQUE KEY unique_topic_grammar_type (topic_id, name),
    INDEX idx_topic_active (topic_id, is_active)
);

CREATE TABLE grammar_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lesson_id INT NOT NULL,
    type_id INT NOT NULL,
    question TEXT NOT NULL,
    options JSON NULL,
    correct_answer TEXT NOT NULL,
    xp_reward INT DEFAULT 5,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES exercise_grammar_type(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES grammar_lessons(id) ON DELETE CASCADE,
    INDEX idx_lesson_active (lesson_id, is_active),
    INDEX idx_type (type_id)
);

CREATE TABLE user_grammar_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    topic_id INT NOT NULL,
    lesson_id INT NOT NULL,
    question_id INT NULL,
    type ENUM('theory','exercise') NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES grammar_lessons(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES grammar_questions(id) ON DELETE SET NULL,
    FOREIGN KEY (topic_id) REFERENCES grammar_topics(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_lesson_question_type (user_id, lesson_id, question_id, type),
    INDEX idx_user_topic (user_id, topic_id),
    INDEX idx_user_activity (user_id, type),
    INDEX idx_completed (is_completed, completed_at)
);

CREATE TABLE writing_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    xp_reward INT DEFAULT 50 CHECK (xp_reward >= 0),
    writing_tips TEXT NULL,
    sample_prompt TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (is_active)
);

CREATE TABLE writing_prompts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    prompt TEXT NOT NULL,
    user_content TEXT NULL,
    word_count INT DEFAULT 0,
    grammar_score INT NULL CHECK (grammar_score >= 0 AND grammar_score <= 100),
    vocabulary_score INT NULL CHECK (vocabulary_score >= 0 AND vocabulary_score <= 100),
    coherence_score INT NULL CHECK (coherence_score >= 0 AND coherence_score <= 100),
    overall_score INT NULL CHECK (overall_score >= 0 AND overall_score <= 100),
    ai_feedback TEXT NULL,
    grammar_suggestions JSON NULL,
    vocabulary_suggestions JSON NULL,
    time_spent INT NULL CHECK (time_spent >= 0),
    submitted_at TIMESTAMP NULL,
    xp_reward INT DEFAULT 50 CHECK (xp_reward >= 0),
    is_completed BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES writing_categories(id) ON DELETE CASCADE,
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_user_category (user_id, category_id),
    INDEX idx_category_active (category_id, is_active),
    INDEX idx_completed (is_completed)
);

CREATE TABLE forum_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    xp_reward INT DEFAULT 20,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_created_at (created_at),
    INDEX idx_likes (likes_count),
    INDEX idx_comments_count (comments_count)
);

CREATE TABLE forum_comments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    parent_id INT NULL,

    content TEXT NOT NULL,
    likes_count INT DEFAULT 0,

    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES forum_comments(id) ON DELETE CASCADE,


    INDEX idx_post_active (post_id, is_active),
    INDEX idx_parent_created (parent_id, created_at),
    INDEX idx_user_created (user_id, created_at)
);

CREATE TABLE forum_post_media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    media_type ENUM('image','file','text') NOT NULL,
    file_name VARCHAR(255) NULL,
    mime_type VARCHAR(100) NULL,
    file_size INT NULL,
    url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500) NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    INDEX idx_post_type (post_id, media_type),
    INDEX idx_post_created (post_id, created_at)
);

CREATE TABLE forum_likes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    target_type ENUM('post', 'comment') NOT NULL,
    target_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    UNIQUE KEY unique_user_like (user_id, target_type, target_id),

    INDEX idx_target (target_type, target_id),
    INDEX idx_user_created (user_id, created_at)
);

CREATE TABLE user_daily_stats (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    date DATE NOT NULL,

    vocab_learned INT DEFAULT 0,
    grammar_completed INT DEFAULT 0,
    exercises_done INT DEFAULT 0,
    writing_submitted INT DEFAULT 0,
    forum_posts INT DEFAULT 0,
    forum_comments INT DEFAULT 0,

    study_time_minutes INT DEFAULT 0,
    xp_earned INT DEFAULT 0,
    accuracy_rate DECIMAL(5,2) NULL,
    is_study_day BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, date),
    INDEX idx_user_date (user_id, date),
    INDEX idx_date_study (date, is_study_day)
);

CREATE TABLE user_streaks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    current_streak INT DEFAULT 0,
    longest_streak INT DEFAULT 0,
    last_activity_date DATE NULL,
    streak_start_date DATE NULL,
    longest_streak_date DATE NULL,
    total_study_days INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_streak (user_id),
    INDEX idx_current_streak (current_streak),
    INDEX idx_longest_streak (longest_streak)
);