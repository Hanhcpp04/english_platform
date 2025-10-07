# API Quản lý Từ Vựng (Vocabulary API)

## Tổng quan
API này cho phép:
1. Lấy danh sách từ vựng theo topic với trạng thái hoàn thành của user
2. Đánh dấu từ vựng đã hoàn thành và tự động cộng XP cho user

---

## 1. API Lấy danh sách từ vựng theo topic

### Endpoint
```
GET /vocab/topic/{topicId}/words?userId={userId}
```

### Mô tả
Lấy tất cả từ vựng của một topic cụ thể kèm theo trạng thái hoàn thành của user đó.

### Parameters
- **topicId** (Path Variable, bắt buộc): ID của topic
- **userId** (Query Parameter, bắt buộc): ID của user

### Response Success (200 OK)
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
      "audioUrl": "https://example.com/audio/apple.mp3",
      "imageUrl": "https://example.com/images/apple.jpg",
      "exampleSentence": "I eat an apple every day.",
      "exampleTranslation": "Tôi ăn một quả táo mỗi ngày.",
      "wordType": "noun",
      "xpReward": 5,
      "isCompleted": true
    },
    {
      "id": 2,
      "englishWord": "banana",
      "vietnameseMeaning": "quả chuối",
      "pronunciation": "/bəˈnænə/",
      "audioUrl": "https://example.com/audio/banana.mp3",
      "imageUrl": "https://example.com/images/banana.jpg",
      "exampleSentence": "Bananas are yellow.",
      "exampleTranslation": "Chuối có màu vàng.",
      "wordType": "noun",
      "xpReward": 5,
      "isCompleted": false
    }
  ]
}
```

### Ví dụ Request (cURL)
```bash
curl -X GET "http://localhost:8080/vocab/topic/1/words?userId=123"
```

### Ví dụ Request (JavaScript/Axios)
```javascript
axios.get('/vocab/topic/1/words', {
  params: { userId: 123 }
})
.then(response => {
  console.log(response.data.result); // Danh sách từ vựng
})
.catch(error => {
  console.error(error);
});
```

---

## 2. API Đánh dấu từ vựng đã hoàn thành

### Endpoint
```
POST /vocab/complete?userId={userId}
```

### Mô tả
Đánh dấu một từ vựng đã hoàn thành và tự động cộng XP (mặc định 5 XP) vào tổng XP của user.

**Lưu ý:** Nếu từ đã được đánh dấu hoàn thành trước đó, sẽ không cộng XP lần nữa.

### Parameters
- **userId** (Query Parameter, bắt buộc): ID của user

### Request Body
```json
{
  "wordId": 2,
  "topicId": 1
}
```

### Response Success (200 OK)
```json
{
  "code": 200,
  "message": "Đánh dấu hoàn thành thành công và cộng 5 XP",
  "result": {
    "id": 2,
    "englishWord": "banana",
    "vietnameseMeaning": "quả chuối",
    "pronunciation": "/bəˈnænə/",
    "audioUrl": "https://example.com/audio/banana.mp3",
    "imageUrl": "https://example.com/images/banana.jpg",
    "exampleSentence": "Bananas are yellow.",
    "exampleTranslation": "Chuối có màu vàng.",
    "wordType": "noun",
    "xpReward": 5,
    "isCompleted": true
  }
}
```

### Ví dụ Request (cURL)
```bash
curl -X POST "http://localhost:8080/vocab/complete?userId=123" \
  -H "Content-Type: application/json" \
  -d '{
    "wordId": 2,
    "topicId": 1
  }'
```

### Ví dụ Request (JavaScript/Axios)
```javascript
axios.post('/vocab/complete', {
  wordId: 2,
  topicId: 1
}, {
  params: { userId: 123 }
})
.then(response => {
  console.log(response.data.message); // "Đánh dấu hoàn thành thành công và cộng 5 XP"
  console.log(response.data.result); // Thông tin từ vừa hoàn thành
})
.catch(error => {
  console.error(error);
});
```

---

## Luồng hoạt động

### Frontend Flow
1. **Hiển thị danh sách từ:**
   - Gọi API `GET /vocab/topic/{topicId}/words?userId={userId}`
   - Hiển thị danh sách từ vựng
   - Hiển thị badge "Hoàn thành" cho các từ có `isCompleted = true`

2. **Khi user nhấn nút "Hoàn thành":**
   - Gọi API `POST /vocab/complete?userId={userId}` với body chứa `wordId` và `topicId`
   - Nhận response với message thông báo đã cộng XP
   - Cập nhật UI: đánh dấu từ là hoàn thành
   - (Optional) Hiển thị animation +5 XP

### Ví dụ React Component
```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function VocabList({ topicId, userId }) {
  const [words, setWords] = useState([]);
  
  useEffect(() => {
    loadWords();
  }, [topicId]);
  
  const loadWords = async () => {
    try {
      const response = await axios.get(`/vocab/topic/${topicId}/words`, {
        params: { userId }
      });
      setWords(response.data.result);
    } catch (error) {
      console.error('Error loading words:', error);
    }
  };
  
  const handleComplete = async (wordId) => {
    try {
      const response = await axios.post('/vocab/complete', 
        { wordId, topicId },
        { params: { userId } }
      );
      
      // Hiển thị thông báo
      alert(response.data.message);
      
      // Reload danh sách để cập nhật trạng thái
      loadWords();
    } catch (error) {
      console.error('Error completing word:', error);
    }
  };
  
  return (
    <div className="vocab-list">
      {words.map(word => (
        <div key={word.id} className="vocab-card">
          <h3>{word.englishWord}</h3>
          <p>{word.vietnameseMeaning}</p>
          <p>{word.pronunciation}</p>
          {word.imageUrl && <img src={word.imageUrl} alt={word.englishWord} />}
          <p><em>{word.exampleSentence}</em></p>
          <p>{word.exampleTranslation}</p>
          
          {word.isCompleted ? (
            <span className="badge-completed">✓ Đã hoàn thành</span>
          ) : (
            <button onClick={() => handleComplete(word.id)}>
              Hoàn thành (+{word.xpReward} XP)
            </button>
          )}
        </div>
      ))}
    </div>
  );
}

export default VocabList;
```

---

## Các file đã tạo

### 1. Entity
- ✅ `VocabUserProgress.java` - Đã cập nhật thêm Lombok annotations

### 2. Repository
- ✅ `VocabWordRepository.java` - Repository cho từ vựng
- ✅ `VocabUserProgressRepository.java` - Đã thêm methods mới

### 3. DTO
- ✅ `VocabWordResponse.java` - Response DTO cho từ vựng
- ✅ `CompleteWordRequest.java` - Request DTO cho việc hoàn thành từ

### 4. Service
- ✅ `VocabWordService.java` - Service xử lý logic

### 5. Controller
- ✅ `VocabController.java` - REST Controller với 2 endpoints

---

## Database Schema

Đảm bảo bạn có bảng `vocab_user_progress` với cấu trúc:

```sql
CREATE TABLE vocab_user_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    question_id BIGINT,
    type ENUM('flashcard', 'exercise') NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (word_id) REFERENCES vocab_words(id),
    FOREIGN KEY (topic_id) REFERENCES vocab_topics(id),
    FOREIGN KEY (question_id) REFERENCES vocab_exercise_questions(id)
);
```

---

## Testing

### Postman Collection
Import các endpoint sau vào Postman để test:

1. **Get Words by Topic**
   - Method: GET
   - URL: `http://localhost:8080/vocab/topic/1/words?userId=1`

2. **Complete Word**
   - Method: POST
   - URL: `http://localhost:8080/vocab/complete?userId=1`
   - Body (JSON):
     ```json
     {
       "wordId": 1,
       "topicId": 1
     }
     ```

---

## Notes
- XP mặc định cho mỗi từ là 5, được định nghĩa trong `VocabWordEntity`
- Khi đánh dấu hoàn thành, XP sẽ tự động cộng vào `total_xp` của `UserEntity`
- Không cộng XP nhiều lần nếu từ đã được đánh dấu hoàn thành trước đó
- Trường `type` trong progress được set mặc định là `flashcard`

