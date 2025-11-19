-- Bảng users: Lưu thông tin tài khoản người dùng, xác thực và gamification (XP, vai trò)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    fullname VARCHAR(255) NOT NULL,
    avatar VARCHAR(500),
    role ENUM('ADMIN', 'USER') DEFAULT 'USER',
    provider VARCHAR(255),
    google_id VARCHAR(255) UNIQUE,
    facebook_id VARCHAR(255) UNIQUE,
    total_xp INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_total_xp (total_xp),
    INDEX idx_role (role)
);

-- Bảng level: Định nghĩa các cấp độ dựa trên điểm kinh nghiệm (XP)
CREATE TABLE level (
    level_number INT PRIMARY KEY,
    level_name VARCHAR(100) NOT NULL,
    min_xp INT NOT NULL,
    max_xp INT NULL,
    description TEXT NULL,
    INDEX idx_xp_range (min_xp, max_xp)
);

-- Bảng badges: Danh sách huy hiệu và điều kiện đạt được
CREATE TABLE badges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    icon_url VARCHAR(500) NULL,
    condition_type ENUM(
        'VOCABULARY',
        'GRAMMAR',
        'LISTENING',
        'READING',
        'WRITING',
        'TESTING',
        'FORUM',
        'STREAK',
        'ACCURACY') NOT NULL,
    condition_value INT NOT NULL,
    xp_reward INT DEFAULT 0 CHECK (xp_reward >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_condition (condition_type, condition_value),
    INDEX idx_active (is_active)
);

-- Bảng user_badges: Liên kết người dùng với các huy hiệu đã đạt được
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

-- Bảng user_badge_progress: Theo dõi tiến độ đạt huy hiệu của người dùng
CREATE TABLE user_badge_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    badge_id INT NOT NULL,
    current_value INT DEFAULT 0,
    target_value INT NOT NULL,
    progress_percentage DECIMAL(5,2) DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_badge_progress (user_id, badge_id),
    INDEX idx_user_progress (user_id, progress_percentage),
    INDEX idx_badge_progress (badge_id, current_value)
);

-- Bảng vocab_topics: Danh sách các chủ đề từ vựng
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

-- Bảng vocab_words: Từ vựng theo từng chủ đề với nghĩa, phát âm, ví dụ
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

-- Bảng vocab_exercise_type: Các loại bài tập từ vựng (trắc nghiệm, ghép từ...)
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

-- Bảng vocab_exercise_questions: Câu hỏi bài tập từ vựng
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

-- Bảng vocab_user_progress: Theo dõi tiến độ học từ vựng và bài tập của người dùng
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
-- grammar
-- Bảng grammar_topics: Danh sách các chủ đề ngữ pháp
CREATE TABLE grammar_topics (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    xp_reward INT DEFAULT 100,
    INDEX idx_active (is_active)
);

-- Bảng grammar_lessons: Bài học ngữ pháp chi tiết theo chủ đề
CREATE TABLE grammar_lessons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    topic_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    xp_reward INT DEFAULT 100,
    is_active TINYINT(1)  DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES grammar_topics(id) ON DELETE CASCADE,
    INDEX idx_topic_active (topic_id, is_active)
);

-- Bảng exercise_grammar_type: Các loại bài tập ngữ pháp
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

-- Bảng grammar_questions: Câu hỏi bài tập ngữ pháp
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
    FOREIGN KEY (lesson_id) REFERENCES grammar_lessons(id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES exercise_grammar_type(id) ON DELETE CASCADE,
    INDEX idx_lesson_active (lesson_id, is_active),
    INDEX idx_type_active (type_id, is_active)
);

-- Bảng user_exercise_answers: Lưu câu trả lời và kết quả bài tập ngữ pháp của người dùng
CREATE TABLE user_exercise_answers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    type_id INT NOT NULL,
    user_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES grammar_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES exercise_grammar_type(id) ON DELETE CASCADE,
    INDEX idx_user_question (user_id, question_id),
    INDEX idx_user_type (user_id, type_id),
    INDEX idx_user_correct (user_id, is_correct)
);



-- Bảng user_grammar_progress: Theo dõi tiến độ học ngữ pháp (lý thuyết và bài tập)
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

-- Bảng writing_topic:
CREATE TABLE writing_topic (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (is_active)
);

CREATE TABLE writing_tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    form_id INT NOT NULL,
    question TEXT NULL,
    writing_tips TEXT NULL,
    xp_reward INT DEFAULT 50 CHECK (xp_reward >= 0),
    is_completed BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_writing_form
        FOREIGN KEY (form_id) REFERENCES writing_form(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    INDEX idx_form (form_id),
    INDEX idx_active (is_active)
);
-- Bảng user_writing: Bài viết của người dùng với điểm số và phản hồi AI
CREATE TABLE writing_prompts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    form_id INT NULL, -- nếu PROMPT thì có form_id, nếu FREE thì null
    mode ENUM('PROMPT', 'FREE') DEFAULT 'PROMPT',
    title VARCHAR(255) NULL,
    user_content TEXT NOT NULL,
    word_count INT DEFAULT 0,
    grammar_score INT CHECK (grammar_score BETWEEN 0 AND 100),
    vocabulary_score INT CHECK (vocabulary_score BETWEEN 0 AND 100),
    coherence_score INT CHECK (coherence_score BETWEEN 0 AND 100),
    overall_score INT CHECK (overall_score BETWEEN 0 AND 100),
    ai_feedback TEXT NULL,
    grammar_suggestions JSON NULL,
    vocabulary_suggestions JSON NULL,
    time_spent INT CHECK (time_spent >= 0),
    xp_reward INT DEFAULT 50 CHECK (xp_reward >= 0),
    is_completed BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    submitted_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (form_id) REFERENCES writing_form(id) ON DELETE SET NULL,
    INDEX idx_user_mode (user_id, mode),
    INDEX idx_completed (is_completed)
);
-- Bảng forum_posts: Bài viết trong diễn đàn
CREATE TABLE forum_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
-- Bảng forum_comments: Bình luận trong diễn đàn (hỗ trợ trả lời đệ quy)
CREATE TABLE forum_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    parent_id INT NULL, -- cái này nó sẽ liên kết đến cái cmt của mình để tạo ra cmt con nó là mối quan hệ đệ quy
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
-- Bảng forum_post_media: Lưu file đính kèm (ảnh, file) của bài đăng
CREATE TABLE forum_post_media (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
-- Bảng forum_post_views: Theo dõi lượt xem bài viết diễn đàn
CREATE TABLE forum_post_views (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id INT NOT NULL,
    user_id INT NULL,
    ip_address VARCHAR(45) NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_post_viewed (post_id, viewed_at),
    INDEX idx_user_post_date (user_id, post_id, viewed_at)
);

-- Bảng user_daily_stats: Thống kê hoạt động học tập hàng ngày của người dùng
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

-- Bảng user_streaks: Theo dõi chuỗi ngày học liên tiếp của người dùng
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