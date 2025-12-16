-- =====================================================
-- SAMPLE BADGES DATA
-- Dữ liệu mẫu cho hệ thống huy hiệu
-- =====================================================

-- ===================
-- VOCABULARY BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('First Word', 'Chúc mừng! Bạn đã học từ vựng đầu tiên', 'VOCABULARY', 1, 10, '/badges/first-word.png', TRUE),
('Word Learner', 'Xuất sắc! Đã học được 50 từ vựng', 'VOCABULARY', 50, 200, '/badges/word-learner.png', TRUE),
('Word Master', 'Tuyệt vời! Đã chinh phục 100 từ vựng', 'VOCABULARY', 100, 500, '/badges/word-master.png', TRUE),
('Vocabulary Pro', 'Đỉnh cao! Hoàn thành 250 từ vựng', 'VOCABULARY', 250, 1000, '/badges/vocab-pro.png', TRUE),
('Vocabulary Expert', 'Huyền thoại! Nắm vững 500 từ vựng', 'VOCABULARY', 500, 2000, '/badges/vocab-expert.png', TRUE),
('Word Champion', 'Vô địch! Đã học 1000 từ vựng', 'VOCABULARY', 1000, 5000, '/badges/word-champion.png', TRUE);

-- ===================
-- GRAMMAR BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('Grammar Newbie', 'Bắt đầu hành trình ngữ pháp!', 'GRAMMAR', 1, 10, '/badges/grammar-newbie.png', TRUE),
('Grammar Student', 'Đã hoàn thành 10 bài học ngữ pháp', 'GRAMMAR', 10, 150, '/badges/grammar-student.png', TRUE),
('Grammar Scholar', 'Giỏi quá! Hoàn thành 20 bài ngữ pháp', 'GRAMMAR', 20, 300, '/badges/grammar-scholar.png', TRUE),
('Grammar Expert', 'Chuyên gia! Đã làm chủ 50 bài ngữ pháp', 'GRAMMAR', 50, 800, '/badges/grammar-expert.png', TRUE),
('Grammar Master', 'Bậc thầy ngữ pháp! 100 bài hoàn thành', 'GRAMMAR', 100, 2000, '/badges/grammar-master.png', TRUE);

-- ===================
-- WRITING BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('First Writer', 'Bài viết đầu tiên của bạn!', 'WRITING', 1, 20, '/badges/first-writer.png', TRUE),
('Budding Author', 'Đã hoàn thành 5 bài viết', 'WRITING', 5, 100, '/badges/budding-author.png', TRUE),
('Regular Writer', 'Chăm chỉ! Đã viết 10 bài', 'WRITING', 10, 300, '/badges/regular-writer.png', TRUE),
('Writing Master', 'Tài năng! 20 bài viết hoàn thành', 'WRITING', 20, 1000, '/badges/writing-master.png', TRUE),
('Writing Legend', 'Huyền thoại! 50 bài viết xuất sắc', 'WRITING', 50, 3000, '/badges/writing-legend.png', TRUE);

-- ===================
-- FORUM BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('Forum Newbie', 'Bài viết đầu tiên trên diễn đàn!', 'FORUM', 1, 10, '/badges/forum-newbie.png', TRUE),
('Active Member', 'Đã tạo 10 bài viết diễn đàn', 'FORUM', 10, 100, '/badges/active-member.png', TRUE),
('Popular Poster', 'Hot! 25 bài viết diễn đàn', 'FORUM', 25, 300, '/badges/popular-poster.png', TRUE),
('Forum Star', 'Ngôi sao diễn đàn! 50 bài viết', 'FORUM', 50, 600, '/badges/forum-star.png', TRUE),
('Community Leader', 'Lãnh đạo cộng đồng! 100 bài viết', 'FORUM', 100, 1500, '/badges/community-leader.png', TRUE);

-- ===================
-- STREAK BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('3 Day Streak', 'Kiên trì! Học liên tục 3 ngày', 'STREAK', 3, 50, '/badges/streak-3.png', TRUE),
('Week Warrior', 'Xuất sắc! Streak 7 ngày', 'STREAK', 7, 300, '/badges/streak-7.png', TRUE),
('Two Weeks Hero', 'Phi thường! Streak 14 ngày', 'STREAK', 14, 500, '/badges/streak-14.png', TRUE),
('Monthly Champion', 'Tuyệt vời! Học đều 30 ngày', 'STREAK', 30, 1500, '/badges/streak-30.png', TRUE),
('Dedication Master', 'Nghị lực thép! Streak 60 ngày', 'STREAK', 60, 3000, '/badges/streak-60.png', TRUE),
('100 Day Legend', 'Huyền thoại! Streak 100 ngày', 'STREAK', 100, 5000, '/badges/streak-100.png', TRUE),
('Half Year Hero', 'Không tưởng! 180 ngày liên tục', 'STREAK', 180, 10000, '/badges/streak-180.png', TRUE),
('Year Long Master', 'Siêu nhân! Streak cả năm (365 ngày)', 'STREAK', 365, 20000, '/badges/streak-365.png', TRUE);

-- ===================
-- ACCURACY BADGES
-- ===================
INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
('Perfect Start', 'Điểm 100% lần đầu tiên!', 'ACCURACY', 1, 50, '/badges/perfect-start.png', TRUE),
('Accuracy Pro', 'Chính xác! 5 lần đạt 100%', 'ACCURACY', 5, 200, '/badges/accuracy-pro.png', TRUE),
('Perfect Score', 'Hoàn hảo! 10 lần đạt 100%', 'ACCURACY', 10, 800, '/badges/perfect-score.png', TRUE),
('Precision Master', 'Bậc thầy! 25 lần đạt 100%', 'ACCURACY', 25, 2000, '/badges/precision-master.png', TRUE),
('Flawless Legend', 'Huyền thoại! 50 lần điểm tuyệt đối', 'ACCURACY', 50, 5000, '/badges/flawless-legend.png', TRUE);

-- ===================
-- SPECIAL/COMBO BADGES (Optional - có thể thêm logic phức tạp hơn)
-- ===================
-- Những badge này cần logic đặc biệt để check, không chỉ đơn giản đếm số lượng

-- Ví dụ: Đạt cả 3 loại hoạt động trong 1 ngày
-- INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
-- ('Daily Achiever', 'Hoàn thành vocab, grammar và writing trong 1 ngày', 'combo_daily', 1, 500, '/badges/daily-achiever.png', TRUE);

-- Ví dụ: Đạt điểm cao trong writing
-- INSERT INTO badges (name, description, condition_type, condition_value, xp_reward, icon_url, is_active) VALUES
-- ('Perfect Writer', 'Đạt điểm 95+ cho 10 bài viết', 'writing_score', 10, 1500, '/badges/perfect-writer.png', TRUE);

-- =====================================================
-- KẾT THÚC SAMPLE DATA
-- =====================================================

-- Query để xem tất cả badges theo loại:
-- SELECT condition_type, COUNT(*) as badge_count, SUM(xp_reward) as total_xp
-- FROM badges
-- WHERE is_active = TRUE
-- GROUP BY condition_type
-- ORDER BY badge_count DESC;

