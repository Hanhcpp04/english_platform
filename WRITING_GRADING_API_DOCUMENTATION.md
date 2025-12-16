# Writing Grading API - Gemini Integration

## 📋 Tổng Quan

Hệ thống chấm điểm bài viết tự động sử dụng Gemini AI API của Google.

## 🚀 API Endpoint

### Submit Writing for Grading
```
POST /api/v1/writing/submit?userId={userId}
Content-Type: application/json
```

**Request Body:**
```json
{
  "taskId": 1,
  "content": "My essay about environmental protection...",
  "mode": "PROMPT",
  "timeSpent": 1800
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "promptId": 123,
    "grammarScore": 85,
    "vocabularyScore": 90,
    "coherenceScore": 88,
    "overallScore": 88,
    "generalFeedback": "Well-written essay with good structure...",
    "grammarSuggestions": [
      {
        "error": "there is many people",
        "suggestion": "there are many people",
        "explanation": "Use 'are' with plural nouns"
      }
    ],
    "vocabularySuggestions": [
      {
        "word": "good",
        "betterAlternative": "beneficial",
        "reason": "More academic and specific"
      }
    ],
    "wordCount": 250,
    "xpEarned": 150,
    "isCompleted": true
  }
}
```

## ⚙️ Configuration

### 1. Get Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click "Create API Key"
3. Copy the API key

### 2. Configure Environment Variables

**Option A: Using .env file (Recommended for production)**
```bash
# Create .env file in project root
GEMINI_API_KEY=AIzaSyBxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**Option B: Using application.yml (Development)**
```yaml
gemini:
  api-key: AIzaSyBxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**Option C: Using System Environment Variable**
```bash
# Windows
set GEMINI_API_KEY=AIzaSyBxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Linux/Mac
export GEMINI_API_KEY=AIzaSyBxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### 3. Configuration Properties

In `application.yml`:
```yaml
gemini:
  api-key: ${GEMINI_API_KEY:your-gemini-api-key-here}
  api-url: https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
  max-tokens: 2048
  temperature: 0.7
```

## 🏗️ Architecture

### Components

1. **SubmitWritingRequest** - DTO cho request
2. **GradingResultResponse** - DTO cho response
3. **GeminiConfig** - Configuration class
4. **GeminiService** - Service gọi Gemini API
5. **WritingService** - Business logic
6. **WritingController** - REST endpoint

### Flow

```
User submits writing
    ↓
WritingController.submitWriting()
    ↓
WritingService.submitAndGradeWriting()
    ↓
GeminiService.gradeWriting()
    ↓
Call Gemini API with grading prompt
    ↓
Parse JSON response
    ↓
Save to database (WritingPromptEntity)
    ↓
Award XP to user
    ↓
Check badges (WRITING)
    ↓
Return GradingResultResponse
```

## 📊 Grading Criteria

### Scores (0-100)

1. **Grammar Score** - Ngữ pháp
   - Sentence structure
   - Tenses
   - Subject-verb agreement
   - Articles usage

2. **Vocabulary Score** - Từ vựng
   - Word choice
   - Variety
   - Appropriateness
   - Academic level

3. **Coherence Score** - Mạch lạc
   - Logical flow
   - Organization
   - Transitions
   - Clarity

4. **Overall Score** - Tổng điểm
   - Average of all scores
   - Relevance to task
   - Content quality

### XP Reward Calculation

```java
if (score >= 90)  → 150% of base XP  (Excellent)
if (score >= 80)  → 120% of base XP  (Good)
if (score >= 60)  → 100% of base XP  (Pass)
if (score < 60)   → 50% of base XP   (Effort)
```

### Completion Criteria

- Writing is marked as "completed" if **overall score >= 60**
- Only completed writings award full XP and trigger badge check

## 🎯 Badge Integration

When a writing is completed (score >= 60):
1. User receives XP based on score
2. Badge system checks for WRITING badges
3. Possible badges:
   - Writer (1 bài) - 20 XP
   - Story Teller (5 bài) - 100 XP
   - Essay Master (15 bài) - 400 XP
   - Writing Expert (30 bài) - 1000 XP
   - Literary Genius (50 bài) - 2500 XP

## 🔧 Frontend Integration

### Example Request (JavaScript/TypeScript)

```typescript
const submitWriting = async (taskId: number, content: string, userId: number) => {
  const response = await fetch(`/api/v1/writing/submit?userId=${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      taskId: taskId,
      content: content,
      mode: 'PROMPT',
      timeSpent: 1800 // 30 minutes in seconds
    })
  });

  const result = await response.json();
  return result.data; // GradingResultResponse
};
```

### Display Results

```typescript
interface GradingResult {
  promptId: number;
  grammarScore: number;
  vocabularyScore: number;
  coherenceScore: number;
  overallScore: number;
  generalFeedback: string;
  grammarSuggestions: GrammarSuggestion[];
  vocabularySuggestions: VocabularySuggestion[];
  wordCount: number;
  xpEarned: number;
  isCompleted: boolean;
}

// Render scores
<div className="scores">
  <div>Grammar: {result.grammarScore}/100</div>
  <div>Vocabulary: {result.vocabularyScore}/100</div>
  <div>Coherence: {result.coherenceScore}/100</div>
  <div>Overall: {result.overallScore}/100</div>
  <div>XP Earned: {result.xpEarned}</div>
</div>

// Render feedback
<div className="feedback">
  <h3>General Feedback</h3>
  <p>{result.generalFeedback}</p>
  
  <h3>Grammar Suggestions</h3>
  {result.grammarSuggestions.map(s => (
    <div key={s.error}>
      <strong>Error:</strong> {s.error}<br/>
      <strong>Suggestion:</strong> {s.suggestion}<br/>
      <strong>Explanation:</strong> {s.explanation}
    </div>
  ))}
  
  <h3>Vocabulary Improvements</h3>
  {result.vocabularySuggestions.map(s => (
    <div key={s.word}>
      Instead of "{s.word}", try "{s.betterAlternative}"
      <br/>Reason: {s.reason}
    </div>
  ))}
</div>
```

## 🧪 Testing

### Manual Testing

```bash
curl -X POST "http://localhost:8088/api/v1/writing/submit?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 1,
    "content": "Environmental protection is very important. We should protect nature. Many people dont care about pollution. We need to change this. Recycling is good for environment.",
    "mode": "PROMPT",
    "timeSpent": 600
  }'
```

### Expected Response

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "promptId": 1,
    "grammarScore": 75,
    "vocabularyScore": 70,
    "coherenceScore": 72,
    "overallScore": 72,
    "generalFeedback": "Your essay addresses the topic but could benefit from more varied sentence structures and advanced vocabulary...",
    "grammarSuggestions": [
      {
        "error": "dont",
        "suggestion": "don't",
        "explanation": "Missing apostrophe in contraction"
      }
    ],
    "vocabularySuggestions": [
      {
        "word": "good",
        "betterAlternative": "beneficial",
        "reason": "More academic and precise"
      }
    ],
    "wordCount": 31,
    "xpEarned": 120,
    "isCompleted": true
  }
}
```

## ⚠️ Error Handling

### Common Errors

1. **Invalid API Key**
```json
{
  "code": 500,
  "message": "Failed to grade writing: Gemini API returned status: 401"
}
```
→ Check your GEMINI_API_KEY

2. **Task Not Found**
```json
{
  "code": 500,
  "message": "Task not found with ID: 999"
}
```
→ Use valid taskId

3. **User Not Found**
```json
{
  "code": 500,
  "message": "User not found with ID: 999"
}
```
→ Use valid userId

4. **Empty Content**
```json
{
  "code": 400,
  "message": "Writing content cannot be empty"
}
```
→ Provide content in request body

## 🔒 Security

### Rate Limiting (Recommended)

Add rate limiting to prevent API abuse:

```java
@RateLimiter(name = "gemini", fallbackMethod = "rateLimitFallback")
public GradingResultResponse submitAndGradeWriting(...) {
    // ...
}
```

### Input Validation

- Content length: max 5000 words
- Sanitize HTML/script tags
- Check for spam/inappropriate content

## 💰 Cost Considerations

### Gemini API Pricing

- Free tier: 60 requests/minute
- Monitor usage at [Google Cloud Console](https://console.cloud.google.com/)

### Optimization Tips

1. **Cache results** - Don't re-grade same content
2. **Batch processing** - Grade multiple essays in one call
3. **Implement retry logic** - Handle rate limits gracefully

## 📝 Database Schema

### writing_prompts table

```sql
ALTER TABLE writing_prompts 
ADD COLUMN grammar_score INT,
ADD COLUMN vocabulary_score INT,
ADD COLUMN coherence_score INT,
ADD COLUMN overall_score INT,
ADD COLUMN ai_feedback TEXT,
ADD COLUMN grammar_suggestions JSON,
ADD COLUMN vocabulary_suggestions JSON;
```

## 🚀 Deployment Checklist

- [ ] Set GEMINI_API_KEY environment variable
- [ ] Test API with sample writing
- [ ] Configure rate limiting
- [ ] Set up monitoring/logging
- [ ] Update database schema
- [ ] Deploy frontend with API integration
- [ ] Test end-to-end flow

## 📚 Resources

- [Gemini API Documentation](https://ai.google.dev/docs)
- [Get API Key](https://makersuite.google.com/app/apikey)
- [Pricing](https://ai.google.dev/pricing)

---

**Last Updated:** December 15, 2025  
**Version:** 1.0
