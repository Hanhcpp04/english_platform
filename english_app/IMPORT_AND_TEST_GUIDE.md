# 🚀 HƯỚNG DẪN IMPORT DỮ LIỆU VÀ TEST API

## 📦 Dữ liệu giả lập đã tạo

File `vocab_sample_data.sql` chứa:

### 1. **3 Users mẫu:**
- **john_doe** (ID: 1) - User thường, 150 XP
- **jane_smith** (ID: 2) - User thường, 320 XP  
- **admin_user** (ID: 3) - Admin, 1000 XP

### 2. **5 Vocab Topics:**
| ID | Tên tiếng Việt | Tên tiếng Anh | Số từ | Icon |
|----|----------------|---------------|-------|------|
| 1  | Trái cây       | Fruits        | 10    | 🍎   |
| 2  | Động vật       | Animals       | 10    | 🐶   |
| 3  | Màu sắc        | Colors        | 8     | 🎨   |
| 4  | Gia đình       | Family        | 10    | 👨‍👩‍👧‍👦 |
| 5  | Thời tiết      | Weather       | 8     | ☀️   |

### 3. **46 Vocab Words:**
Mỗi từ bao gồm:
- Từ tiếng Anh và nghĩa tiếng Việt
- Phát âm (IPA)
- Link audio phát âm (Google Dictionary)
- Link hình ảnh minh họa (Unsplash)
- Câu ví dụ và bản dịch
- Loại từ (noun, adjective)
- XP reward (5 XP/từ)

### 4. **User Progress:**
- **John (User 1)**: Đã hoàn thành 5/10 từ trong topic "Trái cây"
- **Jane (User 2)**: Đã hoàn thành 7/10 từ trong topic "Động vật" và 3/8 từ trong topic "Màu sắc"

---

## 🔧 CÁCH IMPORT DỮ LIỆU

### Bước 1: Khởi động MySQL
Đảm bảo MySQL server đang chạy.

### Bước 2: Chọn Database
```sql
USE your_database_name;
```

### Bước 3: Import dữ liệu
```bash
# Cách 1: Từ MySQL Command Line
mysql -u root -p your_database_name < vocab_sample_data.sql

# Cách 2: Từ MySQL Workbench
# File -> Run SQL Script -> Chọn file vocab_sample_data.sql
```

### Bước 4: Kiểm tra dữ liệu đã import
```sql
-- Xem số lượng
SELECT 'Users' as Table_Name, COUNT(*) as Count FROM users
UNION ALL
SELECT 'Topics', COUNT(*) FROM vocab_topics
UNION ALL
SELECT 'Words', COUNT(*) FROM vocab_words
UNION ALL
SELECT 'Progress', COUNT(*) FROM vocab_user_progress;

-- Xem chi tiết topics
SELECT * FROM vocab_topics;

-- Xem từ vựng của topic Trái cây
SELECT * FROM vocab_words WHERE topic_id = 1;
```

---

## 🧪 TEST API VỚI POSTMAN

### Test 1: Lấy danh sách từ vựng của User 1 trong topic "Trái cây"

**Request:**
```
GET http://localhost:8080/vocab/topic/1/words?userId=1
```

**Expected Response:**
```json
{
  "code": 200,
  "message": "Lấy danh sách từ vựng thành công",
  "result": [
    {
      "id": 1,
      "englishWord": "apple",
      "vietnameseMeaning": "quả táo",
      "pronunciation": "/ˈæpl/",
      "audioUrl": "https://ssl.gstatic.com/dictionary/static/sounds/20200429/apple--_us_1.mp3",
      "imageUrl": "https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=400",
      "exampleSentence": "I eat an apple every day.",
      "exampleTranslation": "Tôi ăn một quả táo mỗi ngày.",
      "wordType": "noun",
      "xpReward": 5,
      "isCompleted": true    ⬅️ John đã hoàn thành từ này
    },
    {
      "id": 6,
      "englishWord": "watermelon",
      "vietnameseMeaning": "quả dưa hấu",
      ...
      "isCompleted": false   ⬅️ John chưa hoàn thành từ này
    }
    ...
  ]
}
```

### Test 2: User 1 hoàn thành từ "watermelon"

**Request:**
```
POST http://localhost:8080/vocab/complete?userId=1
Content-Type: application/json

{
  "wordId": 6,
  "topicId": 1
}
```

**Expected Response:**
```json
{
  "code": 200,
  "message": "Đánh dấu hoàn thành thành công và cộng 5 XP",
  "result": {
    "id": 6,
    "englishWord": "watermelon",
    "vietnameseMeaning": "quả dưa hấu",
    "pronunciation": "/ˈwɔːtərmelən/",
    "audioUrl": "https://ssl.gstatic.com/dictionary/static/sounds/20200429/watermelon--_us_1.mp3",
    "imageUrl": "https://images.unsplash.com/photo-1587049352846-4a222e784l56?w=400",
    "exampleSentence": "Watermelon is perfect for summer.",
    "exampleTranslation": "Dưa hấu rất thích hợp cho mùa hè.",
    "wordType": "noun",
    "xpReward": 5,
    "isCompleted": true   ⬅️ Đã được đánh dấu hoàn thành
  }
}
```

**Kiểm tra XP đã tăng:**
```sql
SELECT username, total_xp FROM users WHERE id = 1;
-- Trước: 150 XP
-- Sau: 155 XP (đã cộng 5 XP)
```

### Test 3: Gọi lại API complete với từ đã hoàn thành

**Request:**
```
POST http://localhost:8080/vocab/complete?userId=1
Content-Type: application/json

{
  "wordId": 1,
  "topicId": 1
}
```

**Expected:** XP không tăng thêm vì từ này đã được đánh dấu hoàn thành trước đó.

### Test 4: Lấy danh sách từ của topic khác (Animals)

**Request:**
```
GET http://localhost:8080/vocab/topic/2/words?userId=1
```

**Expected:** Tất cả từ có `isCompleted: false` vì User 1 chưa học topic này.

### Test 5: Lấy danh sách từ của User 2

**Request:**
```
GET http://localhost:8080/vocab/topic/2/words?userId=2
```

**Expected:** 7 từ đầu tiên có `isCompleted: true`, các từ còn lại là `false`.

---

## 📊 CÁC SCENARIOS TEST

### Scenario 1: User mới bắt đầu học topic
```
1. GET /vocab/topic/1/words?userId=3
   ➡️ Tất cả từ đều isCompleted: false
   
2. POST /vocab/complete?userId=3 với wordId=1
   ➡️ Đánh dấu hoàn thành, cộng 5 XP
   
3. GET /vocab/topic/1/words?userId=3
   ➡️ Từ với id=1 giờ là isCompleted: true
```

### Scenario 2: User hoàn thành tất cả từ trong topic
```
1. Gọi POST /vocab/complete lần lượt cho tất cả 10 từ trong topic
2. Kiểm tra total_xp tăng 50 XP (10 từ × 5 XP)
3. GET /vocab/topic/1/words?userId=X
   ➡️ Tất cả từ đều isCompleted: true
```

### Scenario 3: Nhiều user học cùng topic
```
1. User 1 hoàn thành từ A
2. User 2 hoàn thành từ A
3. Cả 2 đều được cộng XP riêng biệt
4. Progress của họ không ảnh hưởng lẫn nhau
```

---

## 🐛 TROUBLESHOOTING

### Lỗi: "User không tồn tại"
**Nguyên nhân:** userId không có trong database  
**Giải pháp:** Kiểm tra userId, sử dụng 1, 2, hoặc 3 từ dữ liệu mẫu

### Lỗi: "Từ vựng không tồn tại"
**Nguyên nhân:** wordId không hợp lệ  
**Giải pháp:** Kiểm tra danh sách từ trong topic bằng API GET trước

### Lỗi: "Topic không tồn tại"
**Nguyên nhân:** topicId không hợp lệ  
**Giải pháp:** Sử dụng topicId từ 1-5

### XP không tăng
**Nguyên nhân:** Từ đã được đánh dấu hoàn thành trước đó  
**Giải pháp:** Kiểm tra bảng `vocab_user_progress` xem record đã tồn tại chưa

---

## 📝 QUERIES HỮU ÍCH

### Xem progress của một user
```sql
SELECT 
    u.username,
    vt.name as topic_name,
    vw.english_word,
    vw.vietnamese_meaning,
    vup.is_completed,
    vup.completed_at,
    vw.xp_reward
FROM vocab_user_progress vup
JOIN users u ON vup.user_id = u.id
JOIN vocab_words vw ON vup.word_id = vw.id
JOIN vocab_topics vt ON vup.topic_id = vt.id
WHERE u.id = 1
ORDER BY vup.completed_at DESC;
```

### Thống kê progress theo topic
```sql
SELECT 
    vt.name as topic_name,
    COUNT(DISTINCT vw.id) as total_words,
    COUNT(DISTINCT CASE WHEN vup.is_completed = TRUE THEN vup.word_id END) as completed_words,
    ROUND(COUNT(DISTINCT CASE WHEN vup.is_completed = TRUE THEN vup.word_id END) * 100.0 / COUNT(DISTINCT vw.id), 2) as completion_percentage
FROM vocab_topics vt
LEFT JOIN vocab_words vw ON vt.id = vw.topic_id
LEFT JOIN vocab_user_progress vup ON vw.id = vup.word_id AND vup.user_id = 1
GROUP BY vt.id, vt.name;
```

### Top users theo XP
```sql
SELECT 
    username,
    total_xp,
    RANK() OVER (ORDER BY total_xp DESC) as ranking
FROM users
WHERE is_active = TRUE
ORDER BY total_xp DESC;
```

### Reset progress của user (để test lại)
```sql
-- Xóa progress
DELETE FROM vocab_user_progress WHERE user_id = 1;

-- Reset XP về 0
UPDATE users SET total_xp = 0 WHERE id = 1;
```

---

## ✅ CHECKLIST TEST

- [ ] Import dữ liệu thành công
- [ ] API GET trả về danh sách từ với trạng thái đúng
- [ ] API POST đánh dấu hoàn thành và cộng XP
- [ ] Không cộng XP nhiều lần cho cùng một từ
- [ ] Progress của các user độc lập với nhau
- [ ] Tất cả URL hình ảnh và audio hiển thị đúng
- [ ] Response time < 500ms
- [ ] Error handling hoạt động (userId/wordId không tồn tại)

---

## 🎉 KẾT LUẬN

Bạn đã có:
✅ 46 từ vựng thực tế với hình ảnh và audio
✅ 3 users với progress khác nhau để test
✅ 5 topics đa dạng
✅ API hoàn chỉnh để quản lý học từ vựng

**Chúc bạn test thành công!** 🚀

