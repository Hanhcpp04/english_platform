-- Sample Forum Data
-- Đảm bảo đã có users trong database trước khi chạy script này

-- Sample Posts
INSERT INTO forum_posts (user_id, title, content, likes_count, comments_count, xp_reward, is_active, created_at, updated_at)
VALUES
(1, 'Bí quyết cải thiện kỹ năng nói tiếng Anh hiệu quả trong 30 ngày',
'Xin chào mọi người! Hôm nay mình muốn chia sẻ một số bí quyết giúp mình cải thiện kỹ năng nói tiếng Anh trong vòng 30 ngày.

1. Luyện nói mỗi ngày ít nhất 30 phút
2. Ghi âm và nghe lại giọng nói của mình
3. Tham gia các nhóm speaking club
4. Xem phim và bắt chước cách nói của diễn viên
5. Không sợ sai, cứ tự tin nói

Các bạn có thêm tips nào không? Chia sẻ nhé!',
127, 23, 20, TRUE, NOW() - INTERVAL 15 MINUTE, NOW() - INTERVAL 15 MINUTE),

(2, 'Làm thế nào để học từ vựng hiệu quả?',
'Mình đang gặp khó khăn trong việc nhớ từ vựng. Mỗi lần học xong một thời gian là quên. Các bạn có phương pháp nào hiệu quả không?

Hiện tại mình đang:
- Dùng flashcard
- Viết từ vựng ra giấy
- Làm bài tập

Nhưng vẫn cảm thấy không hiệu quả lắm. Mong được chia sẻ kinh nghiệm!',
89, 15, 20, TRUE, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),

(1, 'Grammar Tips: Phân biệt Present Perfect và Past Simple',
'Nhiều bạn hay nhầm lẫn giữa Present Perfect và Past Simple. Mình xin tóm tắt:

**Present Perfect (have/has + V3)**
- Hành động bắt đầu từ quá khứ, kéo dài đến hiện tại
- Không có thời gian cụ thể
- VD: I have learned English for 5 years.

**Past Simple (V2/ed)**
- Hành động hoàn thành trong quá khứ
- Có thời gian cụ thể
- VD: I learned English last year.

Hi vọng giúp ích cho các bạn!',
156, 31, 20, TRUE, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR),

(3, 'Chia sẻ tài liệu luyện thi IELTS miễn phí',
'Mình vừa thi IELTS đạt 7.5 và muốn chia sẻ một số tài liệu đã giúp mình:

1. Cambridge IELTS 15-18
2. Simon Writing Task 2 samples
3. Podcast BBC Learning English
4. Youtube channel: English with Lucy

Các bạn có thể tìm tất cả miễn phí trên mạng. Chúc các bạn học tốt!',
203, 47, 20, TRUE, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),

(2, 'Listening: Cách để nghe hiểu native speakers',
'Native speakers nói rất nhanh và mình thường không nghe hiểu. Các bạn có tips nào không?

Mình đã thử:
- Xem phim có phụ đề
- Nghe podcast chậm
- Luyện shadow speaking

Nhưng vẫn thấy khó. Help me!',
67, 12, 20, TRUE, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR),

(3, 'Top 10 idioms thường gặp trong giao tiếp',
'Chia sẻ 10 idioms mà mình hay dùng:

1. Break the ice - Phá vỡ sự im lặng
2. Hit the books - Học hành chăm chỉ
3. Call it a day - Kết thúc công việc
4. Piece of cake - Dễ như ăn bánh
5. Cost an arm and a leg - Rất đắt
6. Under the weather - Không khỏe
7. Break a leg - Chúc may mắn
8. Spill the beans - Tiết lộ bí mật
9. On cloud nine - Rất hạnh phúc
10. Once in a blue moon - Hiếm khi

Các bạn biết thêm idiom nào thú vị không?',
178, 29, 20, TRUE, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),

(1, 'Kinh nghiệm học tiếng Anh qua TikTok',
'Có ai học tiếng Anh qua TikTok không? Mình thấy có nhiều creator hay lắm:

- @englishwithtiffani
- @learnenglishwithkristina
- @mrduanmorgan

Video ngắn, dễ nhớ, và rất thú vị. Các bạn follow thêm channel nào hay nữa không?',
98, 18, 20, TRUE, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR),

(2, 'Reading comprehension: Cách đọc hiểu nhanh',
'Tips để cải thiện Reading:

1. Đọc skimming trước (đọc lướt toàn bài)
2. Đọc scanning để tìm thông tin cụ thể
3. Đoán nghĩa từ vựng qua context
4. Không dịch từng từ
5. Luyện đọc mỗi ngày 30 phút

Bắt đầu với các bài báo ngắn, sau đó tăng độ khó dần nhé!',
134, 21, 20, TRUE, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),

(3, 'Motivation: Làm sao để không bỏ cuộc?',
'Học tiếng Anh lâu rồi nhưng mình hay nản. Các bạn có cách nào để giữ động lực không?

Mình thử:
- Đặt mục tiêu nhỏ
- Học cùng bạn bè
- Thưởng cho bản thân khi đạt target

Nhưng vẫn hay bỏ dở. Có phương pháp nào bền vững hơn không?',
112, 25, 20, TRUE, NOW() - INTERVAL 8 HOUR, NOW() - INTERVAL 8 HOUR),

(1, 'Apps học tiếng Anh miễn phí tốt nhất 2025',
'Top 5 apps mình recommend:

1. **Duolingo** - Gamification tốt, miễn phí
2. **Memrise** - Học từ vựng qua video native
3. **HelloTalk** - Chat với native speakers
4. **BBC Learning English** - Nội dung chất lượng
5. **Cake** - Học qua video ngắn

Các bạn đang dùng app nào? Review giúp mình nhé!',
187, 36, 20, TRUE, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR);

-- Sample Comments for Post 1
INSERT INTO forum_comments (post_id, user_id, parent_id, content, likes_count, is_active, created_at, updated_at)
VALUES
(1, 2, NULL, 'Bài viết rất hay! Mình thích tip số 5: Không sợ sai. Đúng là phải tự tin mới tiến bộ được.', 15, TRUE, NOW() - INTERVAL 10 MINUTE, NOW() - INTERVAL 10 MINUTE),
(1, 3, NULL, 'Mình thêm một tip nữa: Luyện nói trước gương hoặc tự nói chuyện với bản thân bằng tiếng Anh!', 22, TRUE, NOW() - INTERVAL 8 MINUTE, NOW() - INTERVAL 8 MINUTE),
(1, 1, 2, 'Cảm ơn bạn! Đúng vậy, mindset rất quan trọng trong việc học ngoại ngữ.', 8, TRUE, NOW() - INTERVAL 5 MINUTE, NOW() - INTERVAL 5 MINUTE),
(1, 2, 3, 'Mình cũng hay làm vậy! Ban đầu thấy kỳ kỳ nhưng hiệu quả thật.', 5, TRUE, NOW() - INTERVAL 3 MINUTE, NOW() - INTERVAL 3 MINUTE);

-- Sample Comments for Post 2
INSERT INTO forum_comments (post_id, user_id, parent_id, content, likes_count, is_active, created_at, updated_at)
VALUES
(2, 1, NULL, 'Bạn thử phương pháp spaced repetition xem. Dùng app Anki sẽ tự động nhắc review từ vựng theo chu kỳ khoa học.', 18, TRUE, NOW() - INTERVAL 50 MINUTE, NOW() - INTERVAL 50 MINUTE),
(2, 3, NULL, 'Mình học từ vựng qua câu chuyện và context thay vì học từ đơn lẻ. Nhớ lâu hơn nhiều!', 12, TRUE, NOW() - INTERVAL 45 MINUTE, NOW() - INTERVAL 45 MINUTE),
(2, 2, 1, 'Thanks! Mình sẽ thử Anki. Có guide nào cho người mới bắt đầu không?', 3, TRUE, NOW() - INTERVAL 40 MINUTE, NOW() - INTERVAL 40 MINUTE);

-- Sample Comments for Post 3
INSERT INTO forum_comments (post_id, user_id, parent_id, content, likes_count, is_active, created_at, updated_at)
VALUES
(3, 2, NULL, 'Giải thích rất rõ ràng! Mình hay nhầm 2 thì này lắm. Cảm ơn bạn nhiều!', 25, TRUE, NOW() - INTERVAL 1 HOUR - INTERVAL 50 MINUTE, NOW() - INTERVAL 1 HOUR - INTERVAL 50 MINUTE),
(3, 3, NULL, 'Bạn có thể cho thêm ví dụ về Present Perfect Continuous không?', 10, TRUE, NOW() - INTERVAL 1 HOUR - INTERVAL 45 MINUTE, NOW() - INTERVAL 1 HOUR - INTERVAL 45 MINUTE);

-- Sample Likes for Posts
INSERT INTO forum_likes (user_id, target_type, target_id, created_at)
VALUES
-- Likes for Post 1
(1, 'post', 1, NOW() - INTERVAL 14 MINUTE),
(2, 'post', 1, NOW() - INTERVAL 13 MINUTE),
(3, 'post', 1, NOW() - INTERVAL 12 MINUTE),

-- Likes for Post 2
(1, 'post', 2, NOW() - INTERVAL 55 MINUTE),
(3, 'post', 2, NOW() - INTERVAL 50 MINUTE),

-- Likes for Post 3
(2, 'post', 3, NOW() - INTERVAL 1 HOUR - INTERVAL 55 MINUTE),
(3, 'post', 3, NOW() - INTERVAL 1 HOUR - INTERVAL 50 MINUTE),

-- Likes for Comments
(1, 'comment', 1, NOW() - INTERVAL 9 MINUTE),
(3, 'comment', 1, NOW() - INTERVAL 8 MINUTE),
(2, 'comment', 2, NOW() - INTERVAL 7 MINUTE),
(1, 'comment', 2, NOW() - INTERVAL 6 MINUTE);

-- Note:
-- Đảm bảo rằng bạn đã có users với id 1, 2, 3 trong bảng users
-- Có thể cần điều chỉnh user_id phù hợp với database của bạn

