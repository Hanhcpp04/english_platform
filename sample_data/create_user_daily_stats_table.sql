-- Tạo bảng user_daily_stats nếu chưa tồn tại
-- Script này có thể chạy nhiều lần mà không gây lỗi

CREATE TABLE IF NOT EXISTS user_daily_stats (
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

-- Kiểm tra xem bảng đã có dữ liệu chưa
SELECT COUNT(*) as total_records FROM user_daily_stats;
