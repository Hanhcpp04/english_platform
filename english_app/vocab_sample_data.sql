-- =====================================================
-- DỮ LIỆU GIẢ LẬP CHO HỆ THỐNG TỪ VỰNG
-- =====================================================

-- 1. TẠO USER MẪU
-- =====================================================
INSERT INTO users (username, email, password_hash, fullname, avatar, role, total_xp, is_active) VALUES
('john_doe', 'john@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'John Doe', 'https://i.pravatar.cc/150?img=1', 'USER', 150, TRUE),
('jane_smith', 'jane@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'Jane Smith', 'https://i.pravatar.cc/150?img=2', 'USER', 320, TRUE),
('admin_user', 'admin@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'Admin User', 'https://i.pravatar.cc/150?img=3', 'ADMIN', 1000, TRUE);

-- 2. TẠO VOCAB TOPICS
-- =====================================================
INSERT INTO vocab_topics (name, english_name, description, icon_url, xp_reward, total_words, is_active) VALUES
('Trái cây', 'Fruits', 'Học từ vựng về các loại trái cây phổ biến', '🍎', 100, 10, TRUE),
('Động vật', 'Animals', 'Học từ vựng về các loại động vật', '🐶', 100, 10, TRUE),
('Màu sắc', 'Colors', 'Học từ vựng về các màu sắc cơ bản', '🎨', 50, 8, TRUE),
('Gia đình', 'Family', 'Học từ vựng về các thành viên trong gia đình', '👨‍👩‍👧‍👦', 80, 10, TRUE),
('Thời tiết', 'Weather', 'Học từ vựng về thời tiết và khí hậu', '☀️', 60, 8, TRUE);

-- 3. TẠO VOCAB WORDS - TOPIC: TRÁI CÂY
-- =====================================================
INSERT INTO vocab_words (topic_id, english_word, vietnamese_meaning, pronunciation, audio_url, image_url, example_sentence, example_translation, word_type, xp_reward, is_active) VALUES
(1, 'apple', 'quả táo', '/ˈæpl/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/apple--_us_1.mp3', 'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=400', 'I eat an apple every day.', 'Tôi ăn một quả táo mỗi ngày.', 'noun', 5, TRUE),
(1, 'banana', 'quả chuối', '/bəˈnænə/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/banana--_us_1.mp3', 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400', 'Bananas are yellow.', 'Chuối có màu vàng.', 'noun', 5, TRUE),
(1, 'orange', 'quả cam', '/ˈɔːrɪndʒ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/orange--_us_1.mp3', 'https://images.unsplash.com/photo-1580052614034-c55d20bfee3b?w=400', 'This orange is very sweet.', 'Quả cam này rất ngọt.', 'noun', 5, TRUE),
(1, 'grape', 'quả nho', '/ɡreɪp/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/grape--_us_1.mp3', 'https://images.unsplash.com/photo-1599819177831-c8ccfd5e3d7e?w=400', 'I love eating grapes.', 'Tôi thích ăn nho.', 'noun', 5, TRUE),
(1, 'strawberry', 'quả dâu tây', '/ˈstrɔːberi/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/strawberry--_us_1.mp3', 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400', 'Strawberries are red and delicious.', 'Dâu tây màu đỏ và rất ngon.', 'noun', 5, TRUE),
(1, 'watermelon', 'quả dưa hấu', '/ˈwɔːtərmelən/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/watermelon--_us_1.mp3', 'https://images.unsplash.com/photo-1587049352846-4a222e784l56?w=400', 'Watermelon is perfect for summer.', 'Dưa hấu rất thích hợp cho mùa hè.', 'noun', 5, TRUE),
(1, 'mango', 'quả xoài', '/ˈmæŋɡoʊ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/mango--_us_1.mp3', 'https://images.unsplash.com/photo-1553279768-865429fa0078?w=400', 'Mango is my favorite fruit.', 'Xoài là loại trái cây yêu thích của tôi.', 'noun', 5, TRUE),
(1, 'pineapple', 'quả dứa/thơm', '/ˈpaɪnæpl/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/pineapple--_us_1.mp3', 'https://images.unsplash.com/photo-1550828520-4cb496926fc9?w=400', 'Pineapple tastes sweet and sour.', 'Dứa có vị ngọt và chua.', 'noun', 5, TRUE),
(1, 'cherry', 'quả anh đào', '/ˈtʃeri/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/cherry--_us_1.mp3', 'https://images.unsplash.com/photo-1528821128474-27f963b062bf?w=400', 'Cherries are small and round.', 'Anh đào nhỏ và tròn.', 'noun', 5, TRUE),
(1, 'peach', 'quả đào', '/piːtʃ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/peach--_us_1.mp3', 'https://images.unsplash.com/photo-1629828874514-59bfad8f2b32?w=400', 'The peach is soft and juicy.', 'Quả đào mềm và nhiều nước.', 'noun', 5, TRUE);

-- 4. TẠO VOCAB WORDS - TOPIC: ĐỘNG VẬT
-- =====================================================
INSERT INTO vocab_words (topic_id, english_word, vietnamese_meaning, pronunciation, audio_url, image_url, example_sentence, example_translation, word_type, xp_reward, is_active) VALUES
(2, 'dog', 'con chó', '/dɔːɡ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/dog--_us_1.mp3', 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=400', 'My dog is very friendly.', 'Con chó của tôi rất thân thiện.', 'noun', 5, TRUE),
(2, 'cat', 'con mèo', '/kæt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/cat--_us_1.mp3', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=400', 'The cat is sleeping on the sofa.', 'Con mèo đang ngủ trên ghế sofa.', 'noun', 5, TRUE),
(2, 'bird', 'con chim', '/bɜːrd/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/bird--_us_1.mp3', 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=400', 'Birds can fly in the sky.', 'Chim có thể bay trên bầu trời.', 'noun', 5, TRUE),
(2, 'fish', 'con cá', '/fɪʃ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/fish--_us_1.mp3', 'https://images.unsplash.com/photo-1535591273668-578e31182c4f?w=400', 'Fish live in the water.', 'Cá sống trong nước.', 'noun', 5, TRUE),
(2, 'elephant', 'con voi', '/ˈelɪfənt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/elephant--_us_1.mp3', 'https://images.unsplash.com/photo-1564760055775-d63b17a55c44?w=400', 'Elephants are very big animals.', 'Voi là loài động vật rất to lớn.', 'noun', 5, TRUE),
(2, 'lion', 'con sư tử', '/ˈlaɪən/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/lion--_us_1.mp3', 'https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=400', 'The lion is the king of the jungle.', 'Sư tử là vua của rừng xanh.', 'noun', 5, TRUE),
(2, 'monkey', 'con khỉ', '/ˈmʌŋki/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/monkey--_us_1.mp3', 'https://images.unsplash.com/photo-1540573133985-87b6da6d54a9?w=400', 'Monkeys like to eat bananas.', 'Khỉ thích ăn chuối.', 'noun', 5, TRUE),
(2, 'rabbit', 'con thỏ', '/ˈræbɪt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/rabbit--_us_1.mp3', 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=400', 'The rabbit has long ears.', 'Con thỏ có đôi tai dài.', 'noun', 5, TRUE),
(2, 'tiger', 'con hổ', '/ˈtaɪɡər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/tiger--_us_1.mp3', 'https://images.unsplash.com/photo-1561731216-c3a4d99437d5?w=400', 'Tigers have orange and black stripes.', 'Hổ có sọc cam và đen.', 'noun', 5, TRUE),
(2, 'horse', 'con ngựa', '/hɔːrs/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/horse--_us_1.mp3', 'https://images.unsplash.com/photo-1553284965-83fd3e82fa5a?w=400', 'Horses can run very fast.', 'Ngựa có thể chạy rất nhanh.', 'noun', 5, TRUE);

-- 5. TẠO VOCAB WORDS - TOPIC: MÀU SẮC
-- =====================================================
INSERT INTO vocab_words (topic_id, english_word, vietnamese_meaning, pronunciation, audio_url, image_url, example_sentence, example_translation, word_type, xp_reward, is_active) VALUES
(3, 'red', 'màu đỏ', '/red/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/red--_us_1.mp3', 'https://images.unsplash.com/photo-1614036417651-e4c38e0174a9?w=400', 'The apple is red.', 'Quả táo màu đỏ.', 'adjective', 5, TRUE),
(3, 'blue', 'màu xanh dương', '/bluː/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/blue--_us_1.mp3', 'https://images.unsplash.com/photo-1535083783855-76ae62b2914e?w=400', 'The sky is blue.', 'Bầu trời màu xanh dương.', 'adjective', 5, TRUE),
(3, 'green', 'màu xanh lá', '/ɡriːn/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/green--_us_1.mp3', 'https://images.unsplash.com/photo-1505820013142-f86a3439c5b2?w=400', 'Grass is green.', 'Cỏ có màu xanh lá.', 'adjective', 5, TRUE),
(3, 'yellow', 'màu vàng', '/ˈjeloʊ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/yellow--_us_1.mp3', 'https://images.unsplash.com/photo-1565699142155-099dfe7834e5?w=400', 'Bananas are yellow.', 'Chuối có màu vàng.', 'adjective', 5, TRUE),
(3, 'black', 'màu đen', '/blæk/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/black--_us_1.mp3', 'https://images.unsplash.com/photo-1523741543316-beb7fc7023d8?w=400', 'The cat is black.', 'Con mèo màu đen.', 'adjective', 5, TRUE),
(3, 'white', 'màu trắng', '/waɪt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/white--_us_1.mp3', 'https://images.unsplash.com/photo-1618783524744-fa89eca27f1e?w=400', 'Snow is white.', 'Tuyết có màu trắng.', 'adjective', 5, TRUE),
(3, 'purple', 'màu tím', '/ˈpɜːrpl/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/purple--_us_1.mp3', 'https://images.unsplash.com/photo-1557672172-298e090bd0f1?w=400', 'Grapes are purple.', 'Nho có màu tím.', 'adjective', 5, TRUE),
(3, 'orange', 'màu cam', '/ˈɔːrɪndʒ/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/orange--_us_2.mp3', 'https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400', 'The sunset is orange.', 'Hoàng hôn có màu cam.', 'adjective', 5, TRUE);

-- 6. TẠO VOCAB WORDS - TOPIC: GIA ĐÌNH
-- =====================================================
INSERT INTO vocab_words (topic_id, english_word, vietnamese_meaning, pronunciation, audio_url, image_url, example_sentence, example_translation, word_type, xp_reward, is_active) VALUES
(4, 'father', 'bố, cha', '/ˈfɑːðər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/father--_us_1.mp3', 'https://images.unsplash.com/photo-1581579438747-1dc8d17bbce4?w=400', 'My father works in an office.', 'Bố tôi làm việc ở văn phòng.', 'noun', 5, TRUE),
(4, 'mother', 'mẹ', '/ˈmʌðər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/mother--_us_1.mp3', 'https://images.unsplash.com/photo-1580292274004-e0e00c970c72?w=400', 'My mother is a teacher.', 'Mẹ tôi là giáo viên.', 'noun', 5, TRUE),
(4, 'brother', 'anh/em trai', '/ˈbrʌðər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/brother--_us_1.mp3', 'https://images.unsplash.com/photo-1542727313-4f3e99aa2568?w=400', 'I have one younger brother.', 'Tôi có một em trai.', 'noun', 5, TRUE),
(4, 'sister', 'chị/em gái', '/ˈsɪstər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/sister--_us_1.mp3', 'https://images.unsplash.com/photo-1488716820095-cbe80883c496?w=400', 'My sister is in college.', 'Chị tôi đang học đại học.', 'noun', 5, TRUE),
(4, 'grandfather', 'ông', '/ˈɡrænfɑːðər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/grandfather--_us_1.mp3', 'https://images.unsplash.com/photo-1569443693539-175ea9f007e8?w=400', 'My grandfather is 75 years old.', 'Ông tôi 75 tuổi.', 'noun', 5, TRUE),
(4, 'grandmother', 'bà', '/ˈɡrænmʌðər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/grandmother--_us_1.mp3', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400', 'Grandmother makes delicious cookies.', 'Bà làm bánh quy rất ngon.', 'noun', 5, TRUE),
(4, 'son', 'con trai', '/sʌn/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/son--_us_1.mp3', 'https://images.unsplash.com/photo-1519689680058-324335c77eba?w=400', 'He has two sons.', 'Ông ấy có hai con trai.', 'noun', 5, TRUE),
(4, 'daughter', 'con gái', '/ˈdɔːtər/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/daughter--_us_1.mp3', 'https://images.unsplash.com/photo-1504490503529-33e1b0cd4a9f?w=400', 'She has one daughter.', 'Bà ấy có một con gái.', 'noun', 5, TRUE),
(4, 'uncle', 'chú, bác, cậu', '/ˈʌŋkl/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/uncle--_us_1.mp3', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', 'My uncle lives in New York.', 'Chú tôi sống ở New York.', 'noun', 5, TRUE),
(4, 'aunt', 'cô, dì, thím', '/ænt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/aunt--_us_1.mp3', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=400', 'My aunt is a doctor.', 'Cô tôi là bác sĩ.', 'noun', 5, TRUE);

-- 7. TẠO VOCAB WORDS - TOPIC: THỜI TIẾT
-- =====================================================
INSERT INTO vocab_words (topic_id, english_word, vietnamese_meaning, pronunciation, audio_url, image_url, example_sentence, example_translation, word_type, xp_reward, is_active) VALUES
(5, 'sunny', 'nắng', '/ˈsʌni/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/sunny--_us_1.mp3', 'https://images.unsplash.com/photo-1601297183305-6df142704ea2?w=400', 'It is sunny today.', 'Hôm nay trời nắng.', 'adjective', 5, TRUE),
(5, 'rainy', 'mưa', '/ˈreɪni/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/rainy--_us_1.mp3', 'https://images.unsplash.com/photo-1534274988757-a28bf1a57c17?w=400', 'It will be rainy tomorrow.', 'Ngày mai sẽ mưa.', 'adjective', 5, TRUE),
(5, 'cloudy', 'nhiều mây', '/ˈklaʊdi/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/cloudy--_us_1.mp3', 'https://images.unsplash.com/photo-1609710228159-0fa9bd7c0827?w=400', 'The sky is cloudy.', 'Bầu trời nhiều mây.', 'adjective', 5, TRUE),
(5, 'windy', 'nhiều gió', '/ˈwɪndi/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/windy--_us_1.mp3', 'https://images.unsplash.com/photo-1527482797697-8795b05a13fe?w=400', 'It is very windy outside.', 'Bên ngoài rất nhiều gió.', 'adjective', 5, TRUE),
(5, 'snowy', 'có tuyết', '/ˈsnoʊi/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/snowy--_us_1.mp3', 'https://images.unsplash.com/photo-1491002052546-bf38f186af56?w=400', 'It was snowy last winter.', 'Mùa đông năm ngoái có tuyết.', 'adjective', 5, TRUE),
(5, 'hot', 'nóng', '/hɑːt/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/hot--_us_1.mp3', 'https://images.unsplash.com/photo-1584551246679-0daf3d275d6f?w=400', 'Summer is very hot.', 'Mùa hè rất nóng.', 'adjective', 5, TRUE),
(5, 'cold', 'lạnh', '/koʊld/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/cold--_us_1.mp3', 'https://images.unsplash.com/photo-1477601263568-180e2c6d046e?w=400', 'Winter is cold.', 'Mùa đông lạnh.', 'adjective', 5, TRUE),
(5, 'storm', 'bão', '/stɔːrm/', 'https://ssl.gstatic.com/dictionary/static/sounds/20200429/storm--_us_1.mp3', 'https://images.unsplash.com/photo-1527482797697-8795b05a13fe?w=400', 'There was a big storm yesterday.', 'Hôm qua có một cơn bão lớn.', 'noun', 5, TRUE);

-- 8. TẠO DỮ LIỆU TIẾN TRÌNH HỌC TẬP (VOCAB_USER_PROGRESS)
-- User 1 (John) đã hoàn thành một số từ trong topic Trái cây
-- =====================================================
INSERT INTO vocab_user_progress (user_id, word_id, topic_id, type, is_completed, completed_at) VALUES
-- John đã hoàn thành 5 từ đầu tiên trong topic Trái cây
(1, 1, 1, 'flashcard', TRUE, '2025-10-01 10:30:00'),
(1, 2, 1, 'flashcard', TRUE, '2025-10-01 10:35:00'),
(1, 3, 1, 'flashcard', TRUE, '2025-10-01 10:40:00'),
(1, 4, 1, 'flashcard', TRUE, '2025-10-02 09:15:00'),
(1, 5, 1, 'flashcard', TRUE, '2025-10-02 09:20:00'),
-- John chưa hoàn thành các từ còn lại (không có record hoặc is_completed = FALSE)
(1, 6, 1, 'flashcard', FALSE, NULL),
(1, 7, 1, 'flashcard', FALSE, NULL),

-- User 2 (Jane) đã hoàn thành nhiều từ trong topic Động vật
(2, 11, 2, 'flashcard', TRUE, '2025-10-03 14:00:00'),
(2, 12, 2, 'flashcard', TRUE, '2025-10-03 14:05:00'),
(2, 13, 2, 'flashcard', TRUE, '2025-10-03 14:10:00'),
(2, 14, 2, 'flashcard', TRUE, '2025-10-03 14:15:00'),
(2, 15, 2, 'flashcard', TRUE, '2025-10-04 08:30:00'),
(2, 16, 2, 'flashcard', TRUE, '2025-10-04 08:35:00'),
(2, 17, 2, 'flashcard', TRUE, '2025-10-04 08:40:00'),

-- Jane cũng học topic Màu sắc
(2, 21, 3, 'flashcard', TRUE, '2025-10-05 10:00:00'),
(2, 22, 3, 'flashcard', TRUE, '2025-10-05 10:05:00'),
(2, 23, 3, 'flashcard', TRUE, '2025-10-05 10:10:00'),
(2, 24, 3, 'flashcard', FALSE, NULL);

-- =====================================================
-- KẾT THÚC DỮ LIỆU GIẢ LẬP
-- =====================================================

-- KIỂM TRA DỮ LIỆU
SELECT 'Users created:' as Info, COUNT(*) as Count FROM users;
SELECT 'Topics created:' as Info, COUNT(*) as Count FROM vocab_topics;
SELECT 'Words created:' as Info, COUNT(*) as Count FROM vocab_words;
SELECT 'Progress records:' as Info, COUNT(*) as Count FROM vocab_user_progress;

-- XEM PROGRESS CỦA USER 1 (John)
SELECT
    u.username,
    vt.name as topic_name,
    vw.english_word,
    vup.is_completed,
    vup.completed_at
FROM vocab_user_progress vup
JOIN users u ON vup.user_id = u.id
JOIN vocab_words vw ON vup.word_id = vw.id
JOIN vocab_topics vt ON vup.topic_id = vt.id
WHERE u.id = 1
ORDER BY vup.completed_at;

