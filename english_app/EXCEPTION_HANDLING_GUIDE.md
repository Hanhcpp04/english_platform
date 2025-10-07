# Hướng Dẫn Kiểm Soát Lỗi (Exception Handling) - VocabStatsService

## 📋 Tổng Quan

Service `VocabStatsService` đã được cải thiện với hệ thống kiểm soát lỗi toàn diện, sử dụng các custom exception classes có sẵn trong dự án.

## 🎯 Các Loại Exception Được Sử Dụng

### 1. **BadRequestException**
- **Mục đích**: Xử lý các lỗi do input không hợp lệ từ client
- **HTTP Status**: 400 BAD REQUEST
- **Khi nào sử dụng**:
  - UserId hoặc TopicId null hoặc <= 0
  - Dữ liệu đầu vào không đúng format
  - Tham số bắt buộc bị thiếu

**Ví dụ:**
```java
if (userId == null || userId <= 0) {
    log.error("Invalid userId: {}", userId);
    throw new BadRequestException("User ID must be a positive number");
}
```

### 2. **ResourceNotFoundException**
- **Mục đích**: Xử lý khi không tìm thấy resource
- **HTTP Status**: 404 NOT FOUND
- **Khi nào sử dụng**:
  - Topic không tồn tại trong database
  - User không tồn tại
  - Bất kỳ entity nào không tìm thấy

**Ví dụ:**
```java
VocabTopicEntity topic = vocabTopicRepository.findById(topicId)
    .orElseThrow(() -> {
        log.warn("Topic not found with id: {}", topicId);
        return new ResourceNotFoundException("Topic", "id", topicId);
    });
```

### 3. **InternalServerException**
- **Mục đích**: Xử lý các lỗi hệ thống không mong muốn
- **HTTP Status**: 500 INTERNAL SERVER ERROR
- **Khi nào sử dụng**:
  - Lỗi database connection
  - Lỗi trong quá trình xử lý logic
  - Bất kỳ lỗi runtime nào không thuộc loại client error

**Ví dụ:**
```java
catch (Exception e) {
    log.error("Error fetching vocab stats for userId {}: {}", userId, e.getMessage(), e);
    throw new InternalServerException("Failed to fetch vocabulary statistics");
}
```

## 🛡️ Chiến Lược Kiểm Soát Lỗi Trong Service

### **Bước 1: Validation Input**
Luôn validate input đầu tiên trước khi xử lý logic:

```java
// Validate userId
if (userId == null || userId <= 0) {
    log.error("Invalid userId: {}", userId);
    throw new BadRequestException("User ID must be a positive number");
}
```

### **Bước 2: Try-Catch Block**
Bọc toàn bộ logic business trong try-catch:

```java
try {
    // Business logic here
    log.debug("Fetching vocab stats for userId: {}", userId);
    // ... xử lý logic
    
} catch (BadRequestException e) {
    // Re-throw BadRequestException (đã biết trước)
    throw e;
} catch (ResourceNotFoundException e) {
    // Re-throw ResourceNotFoundException (đã biết trước)
    throw e;
} catch (Exception e) {
    // Bắt tất cả lỗi không mong muốn
    log.error("Error fetching vocab stats for userId {}: {}", userId, e.getMessage(), e);
    throw new InternalServerException("Failed to fetch vocabulary statistics");
}
```

### **Bước 3: Null Safety**
Kiểm tra null cho tất cả giá trị có thể null:

```java
// Handle null values from repository
totalWordsLearned = (totalWordsLearned != null) ? totalWordsLearned : 0;
totalXpEarned = (totalXpEarned != null) ? totalXpEarned : 0;

// Check null for collections
if (wordsPerTopic != null) {
    for (Object[] row : wordsPerTopic) {
        if (row != null && row.length >= 2) {
            // Process row
        }
    }
}
```

### **Bước 4: Logging**
Log đầy đủ thông tin để debug:

```java
// Log error với đầy đủ context
log.error("Error processing topic {}: {}", topic.getId(), e.getMessage());

// Log debug cho successful cases
log.debug("Successfully fetched vocab stats for userId: {}", userId);

// Log warn cho các trường hợp đặc biệt
log.warn("Topic not found with id: {}", topicId);
```

### **Bước 5: Graceful Degradation**
Xử lý lỗi từng phần mà không làm crash toàn bộ:

```java
for (VocabTopicEntity topic : allTopics) {
    try {
        // Process each topic
        topicProgress.add(progress);
    } catch (Exception e) {
        log.error("Error processing topic {}: {}", topic.getId(), e.getMessage());
        // Continue processing other topics - không throw exception
    }
}
```

## 📊 Áp Dụng Cho Từng Method

### 1. `getUserVocabStats(Long userId)`
```
INPUT VALIDATION → TRY-CATCH → NULL CHECKS → GRACEFUL DEGRADATION → RETURN
```
- ✅ Validate userId
- ✅ Try-catch toàn bộ logic
- ✅ Handle null từ repository
- ✅ Continue nếu 1 topic bị lỗi

### 2. `getAllTopics()`
```
TRY-CATCH → NULL CHECKS → RETURN
```
- ✅ Try-catch toàn bộ
- ✅ Handle null list
- ✅ Convert safety với convertToDTO

### 3. `getTopicById(Long topicId)`
```
INPUT VALIDATION → TRY-CATCH → RESOURCE CHECK → RETURN
```
- ✅ Validate topicId
- ✅ Try-catch với multiple exception types
- ✅ Throw ResourceNotFoundException nếu không tìm thấy

### 4. `getAllTopicsWithProgress(Long userId)`
```
INPUT VALIDATION → TRY-CATCH → NULL CHECKS → GRACEFUL DEGRADATION → RETURN
```
- ✅ Validate userId
- ✅ Handle null data từ repository
- ✅ Filter null results sau khi process

## 🎨 Best Practices

### ✅ DO (Nên làm)
1. **Validate input sớm nhất có thể**
2. **Sử dụng specific exceptions** (BadRequestException, ResourceNotFoundException)
3. **Log đầy đủ thông tin** (userId, topicId, error message)
4. **Re-throw known exceptions** (BadRequestException, ResourceNotFoundException)
5. **Wrap unknown exceptions** thành InternalServerException
6. **Use null-safe operations** (null checks, Optional)
7. **Graceful degradation** cho list processing

### ❌ DON'T (Không nên làm)
1. ❌ Catch Exception mà không log
2. ❌ Return null thay vì throw exception
3. ❌ Expose internal error details ra client
4. ❌ Ignore validation input
5. ❌ Không handle null values
6. ❌ Log nhưng không throw exception khi cần thiết
7. ❌ Throw generic Exception

## 🔄 Flow Chart Xử Lý Exception

```
Request → Validate Input → BadRequestException?
                          ↓ NO
                     Process Logic
                          ↓
                  Database Access → ResourceNotFoundException?
                          ↓ NO
                    Convert Data → InternalServerException?
                          ↓ NO
                    Return Success
```

## 🧪 Testing Exception Handling

### Test Case 1: Invalid Input
```java
@Test
void testGetUserVocabStats_InvalidUserId() {
    assertThrows(BadRequestException.class, () -> {
        vocabStatsService.getUserVocabStats(-1L);
    });
}
```

### Test Case 2: Resource Not Found
```java
@Test
void testGetTopicById_NotFound() {
    assertThrows(ResourceNotFoundException.class, () -> {
        vocabStatsService.getTopicById(999L);
    });
}
```

### Test Case 3: Database Error
```java
@Test
void testGetUserVocabStats_DatabaseError() {
    when(vocabUserProgressRepository.countWordsLearnedByUserId(any()))
        .thenThrow(new RuntimeException("DB Error"));
    
    assertThrows(InternalServerException.class, () -> {
        vocabStatsService.getUserVocabStats(1L);
    });
}
```

## 📝 Kết Luận

Service hiện tại đã có:
- ✅ Input validation đầy đủ
- ✅ Exception handling toàn diện
- ✅ Logging chi tiết
- ✅ Null safety
- ✅ Graceful degradation
- ✅ Proper exception propagation

Tất cả exceptions sẽ được GlobalExceptionHandler xử lý và trả về response chuẩn cho client.

