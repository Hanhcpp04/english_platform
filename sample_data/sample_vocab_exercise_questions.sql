-- Sample data for vocab_exercise_questions
-- Two variants provided: (A) table HAS topic_id column, (B) table DOES NOT have topic_id column
-- Pick the appropriate section for your schema and run in MySQL client.

-- =========================
-- A) If table has topic_id column
-- =========================

INSERT INTO vocab_exercise_questions (question, options, correct_answer, explanation, xp_reward, type_id, topic_id, is_active, created_at)
VALUES
('What is "father" in Vietnamese?',
 '{"questionVi":"Từ ''father'' trong tiếng Việt là gì?","options":["Cha","Mẹ","Anh","Em"],"correctIndex":0}',
 NULL,
 'Common family word',
 5,
 1, -- type_id = 1 (Multiple Choice)
 1, -- topic_id = 1
 1,
 NOW()),

('What is "mother" in Vietnamese?',
 '{"questionVi":"Từ ''mother'' trong tiếng Việt là gì?","options":["Cha","Mẹ","Bố","Cô"],"correctIndex":1}',
 NULL,
 'Family word',
 5,
 1,
 1,
 1,
 NOW()),

('Arrange letters to form the word for "book"',
 '{"questionVi":"Sắp xếp chữ cái để tạo thành từ 'book'","scrambledLetters":["o","b","o","k"],"hint":"Bắt đầu bằng b"}',
 'book',
 'Word arrangement exercise',
 5,
 2, -- type_id = 2 (Word Arrangement)
 1,
 1,
 NOW()),

('Choose the correct meaning: "apple"',
 '{"questionVi":"Ý nghĩa của từ 'apple' là gì?","options":["Táo","Lá","Cây","Hoa"],"correctIndex":0}',
 NULL,
 'Fruit vocabulary',
 5,
 1,
 2, -- topic_id = 2
 1,
 NOW()),

('Fill the blank with the correct translation for "run"',
 NULL,
 'chạy',
 'Simple translation question without options',
 5,
 3, -- type_id = 3 (Short Answer / Translation)
 2,
 1,
 NOW());

-- Verify inserted rows
SELECT id, type_id, topic_id, question, options, correct_answer, xp_reward, is_active FROM vocab_exercise_questions ORDER BY id DESC LIMIT 10;


-- =========================
-- B) If table DOES NOT have topic_id column
--    (Use this variant if your DB still lacks topic_id)
-- =========================

INSERT INTO vocab_exercise_questions (question, options, correct_answer, explanation, xp_reward, type_id, is_active, created_at)
VALUES
('What is "father" in Vietnamese?',
 '{"questionVi":"Từ ''father'' trong tiếng Việt là gì?","options":["Cha","Mẹ","Anh","Em"],"correctIndex":0}',
 NULL,
 'Common family word',
 5,
 1, -- type_id = 1 (Multiple Choice)
 1,
 NOW()),

('Arrange letters to form the word for "book"',
 '{"questionVi":"Sắp xếp chữ cái để tạo thành từ 'book'","scrambledLetters":["o","b","o","k"],"hint":"Bắt đầu bằng b"}',
 'book',
 'Word arrangement exercise',
 5,
 2, -- type_id = 2 (Word Arrangement)
 1,
 NOW());

-- Verify inserted rows
SELECT id, type_id, question, options, correct_answer, xp_reward, is_active FROM vocab_exercise_questions ORDER BY id DESC LIMIT 10;

-- =========================
-- NOTES / MAPPING
-- - type_id should reference existing rows in `vocab_exercise_types` (e.g., 1 = Multiple Choice, 2 = Word Arrangement)
-- - options column is JSON and for Multiple Choice uses fields:
--     {"questionVi":"...","options":["opt1","opt2",...],"correctIndex":<int>}
-- - for Word Arrangement options JSON uses fields:
--     {"questionVi":"...","scrambledLetters":["a","b","c"],"hint":"..."}
-- - correct_answer is used for answer checking in some types (e.g., word-arrangement or short-answer). For multiple-choice we keep correct_index inside options JSON and set correct_answer = NULL.
-- - xp_reward: integer XP awarded when user answers correctly.
-- - is_active: 1 = active, 0 = inactive
-- - created_at: use NOW() for convenience

-- Run the appropriate block for your schema. If you use topic filtering in queries, ensure the topic_id values match existing topics.

