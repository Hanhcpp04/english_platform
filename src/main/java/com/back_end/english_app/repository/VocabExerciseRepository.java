package com.back_end.english_app.repository;

import com.back_end.english_app.dto.respones.vocabExercise.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VocabExerciseRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    // 1. Lấy danh sách exercise types theo topic
    public List<ExerciseTypeDTO> getExerciseTypesByTopic(Integer topicId, Integer userId) {
        String sql = """
        SELECT 
            vet.id,
            vet.name,
            vet.description,
            COUNT(DISTINCT q.id) AS question_count,
            COUNT(DISTINCT CASE WHEN vup.is_completed = 1 THEN vup.question_id END) AS completed_count
        FROM vocab_exercise_types vet
        INNER JOIN topic_exercise_type tet ON vet.id = tet.exercise_type_id
        LEFT JOIN vocab_exercise_questions q 
            ON vet.id = q.type_id AND q.topic_id = ? AND q.is_active = 1
        LEFT JOIN vocab_user_progress vup 
            ON vup.question_id = q.id 
            AND vup.user_id = ? 
            AND vup.type = 'exercise'
            AND vup.topic_id = ?
        WHERE tet.topic_id = ? 
          AND vet.is_active = 1
        GROUP BY vet.id, vet.name, vet.description
        ORDER BY vet.id
        """;

        List<ExerciseTypeDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> new ExerciseTypeDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("question_count"),
                rs.getInt("completed_count")
        ), topicId, userId, topicId, topicId);

        // Debug logging
        System.out.println("DEBUG getExerciseTypesByTopic - topicId: " + topicId + ", userId: " + userId + ", results size: " + results.size());

        return results;
    }


    // 2. Lấy questions theo type_id và topic_id
    public QuestionsResponse getQuestionsByTypeAndTopic(Integer typeId, Integer topicId, Integer userId) {
        String sqlType = "SELECT name FROM vocab_exercise_types WHERE id = ?";
        String exerciseType = jdbcTemplate.queryForObject(sqlType, String.class, typeId);

        // Debug logging
        System.out.println("DEBUG getQuestionsByTypeAndTopic - typeId: " + typeId +
            ", exerciseType from DB: '" + exerciseType + "'");

        String sql = """
                SELECT 
                    q.id,
                    q.question,
                    q.options,
                    q.correct_answer,
                    q.xp_reward,
                    CASE WHEN vup.is_completed = 1 THEN 1 ELSE 0 END AS is_completed
                FROM vocab_exercise_questions q
                LEFT JOIN vocab_user_progress vup
                    ON vup.question_id = q.id
                    AND vup.user_id = ?
                    AND vup.type = 'exercise'
                    AND vup.topic_id = ?
                WHERE q.type_id = ?
                  AND q.topic_id = ?
                  AND q.is_active = 1
                ORDER BY q.id
        """;

        List<QuestionDTO> questions = jdbcTemplate.query(sql,
                new QuestionRowMapper(exerciseType), userId, topicId, typeId, topicId);

        return new QuestionsResponse(exerciseType, questions);
    }


    private class QuestionRowMapper implements RowMapper<QuestionDTO> {
        private final String exerciseType;

        public QuestionRowMapper(String exerciseType) {
            this.exerciseType = exerciseType;
        }

        @Override
        public QuestionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                QuestionDTO dto = new QuestionDTO();
                dto.setId(rs.getInt("id"));
                dto.setQuestion(rs.getString("question"));
                dto.setXpReward(rs.getInt("xp_reward"));
                dto.setIsCompleted(rs.getBoolean("is_completed"));

                // Parse options JSON
                String optionsJson = rs.getString("options");
                String correctAnswer = rs.getString("correct_answer");

                // Debug logging
                System.out.println("DEBUG QuestionRowMapper - questionId: " + dto.getId() +
                    ", exerciseType: '" + exerciseType + "', optionsJson: " + optionsJson);

                if (optionsJson != null && !optionsJson.trim().isEmpty()) {
                    JsonNode optionsNode = objectMapper.readTree(optionsJson);

                    // Check if it's multiple choice (flexible matching)
                    boolean isMultipleChoice = exerciseType != null &&
                        (exerciseType.toLowerCase().contains("multiple") ||
                         exerciseType.toLowerCase().contains("trắc nghiệm"));

                    // Check if it's word arrangement (flexible matching)
                    boolean isWordArrangement = exerciseType != null &&
                        (exerciseType.toLowerCase().contains("arrangement") ||
                         exerciseType.toLowerCase().contains("sắp xếp"));

                    if (isMultipleChoice) {
                        // Multiple choice format
                        if (optionsNode.has("options")) {
                            dto.setOptions(optionsNode.get("options"));
                        }
                        if (optionsNode.has("correctIndex")) {
                            dto.setCorrectIndex(optionsNode.get("correctIndex").asInt());
                        }
                        if (optionsNode.has("questionVi")) {
                            dto.setQuestionVi(optionsNode.get("questionVi").asText());
                        }
                    } else if (isWordArrangement) {
                        // Word arrangement format - support multiple formats

                        // Format 1: scrambledLetters array ["H","T","F","E","R","A"]
                        JsonNode lettersNode = optionsNode.get("scrambledLetters");
                        if (lettersNode != null && lettersNode.isArray()) {
                            String[] letters = new String[lettersNode.size()];
                            for (int i = 0; i < lettersNode.size(); i++) {
                                letters[i] = lettersNode.get(i).asText();
                            }
                            dto.setScrambledLetters(letters);
                        }

                        // Format 2: scrambled string "HTFERA" - convert to array
                        JsonNode scrambledNode = optionsNode.get("scrambled");
                        if (scrambledNode != null && scrambledNode.isTextual()) {
                            String scrambledWord = scrambledNode.asText();
                            String[] letters = new String[scrambledWord.length()];
                            for (int i = 0; i < scrambledWord.length(); i++) {
                                letters[i] = String.valueOf(scrambledWord.charAt(i));
                            }
                            dto.setScrambledLetters(letters);
                        }

                        // Set correct word from correct_answer or from JSON
                        if (optionsNode.has("correct")) {
                            dto.setCorrectWord(optionsNode.get("correct").asText());
                            dto.setHint(optionsNode.get("correct").asText());
                        } else {
                            dto.setCorrectWord(correctAnswer);
                        }
                        if (optionsNode.has("questionVi")) {
                            dto.setQuestionVi(optionsNode.get("questionVi").asText());
                        } else {
                            dto.setQuestionVi("Sắp xếp các chữ cái để tạo thành từ");
                        }
                    }
                }

                dto.setCorrectAnswer(correctAnswer);
                return dto;
            } catch (Exception e) {
                System.err.println("ERROR in QuestionRowMapper: " + e.getMessage());
                e.printStackTrace();
                throw new SQLException("Error mapping question row", e);
            }
        }
    }

    // 3. Lấy question detail để check answer
    public Optional<QuestionDTO> getQuestionById(Integer questionId) {
        String sql = """
                SELECT
                    q.id,
                    q.question,
                    q.options,
                    q.correct_answer,
                    q.xp_reward,
                    q.type_id,
                    vet.name AS exercise_type
                FROM vocab_exercise_questions q
                INNER JOIN vocab_exercise_types vet ON q.type_id = vet.id
                WHERE q.id = ? AND q.is_active = 1
            """;

        List<QuestionDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                QuestionDTO dto = new QuestionDTO();
                dto.setId(rs.getInt("id"));
                dto.setQuestion(rs.getString("question"));
                dto.setXpReward(rs.getInt("xp_reward"));

                // Get correct_answer from database (used for word arrangement and other text-based questions)
                String correctAnswerFromDB = rs.getString("correct_answer");
                dto.setCorrectAnswer(correctAnswerFromDB);

                System.out.println("DEBUG getQuestionById - questionId: " + dto.getId() +
                    ", correctAnswerFromDB: '" + correctAnswerFromDB + "'");

                // Parse options for correct index (multiple choice)
                String optionsJson = rs.getString("options");
                if (optionsJson != null && !optionsJson.trim().isEmpty()) {
                    JsonNode optionsNode = objectMapper.readTree(optionsJson);

                    // Store the full options array for matching text answers
                    if (optionsNode.has("options")) {
                        dto.setOptions(optionsNode.get("options"));
                    }

                    if (optionsNode.has("correctIndex")) {
                        Integer correctIndex = optionsNode.get("correctIndex").asInt();
                        dto.setCorrectIndex(correctIndex);
                        System.out.println("DEBUG getQuestionById - correctIndex from JSON: " + correctIndex);
                    }
                }
                return dto;
            } catch (Exception e) {
                System.err.println("ERROR in getQuestionById: " + e.getMessage());
                e.printStackTrace();
                throw new SQLException("Error mapping question detail", e);
            }
        }, questionId);

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    // 4. Get progress detail
    public ProgressDetail getProgressDetail(Integer typeId, Integer userId) {
        String sql = """
            SELECT 
                COUNT(q.id) as total_questions,
                COUNT(CASE 
                    WHEN vup.is_completed = 1 
                    THEN 1 
                END) as completed_questions
            FROM vocab_exercise_questions q
            LEFT JOIN vocab_user_progress vup ON 
                vup.question_id = q.id 
                AND vup.user_id = ? 
                AND vup.type = 'exercise'
            WHERE q.type_id = ? AND q.is_active = 1
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            int total = rs.getInt("total_questions");
            int completed = rs.getInt("completed_questions");
            int percentage = total > 0 ? (completed * 100 / total) : 0;
            return new ProgressDetail(completed, total, percentage);
        }, userId, typeId);
    }

    // 5. Update user progress - mark question as completed
    public void updateUserProgress(Integer userId, Integer questionId, Integer topicId) {
        String sql = """
            INSERT INTO vocab_user_progress 
                (user_id, topic_id, question_id, type, is_completed, completed_at)
            VALUES (?, ?, ?, 'exercise', 1, NOW())
            ON DUPLICATE KEY UPDATE 
                is_completed = 1,
                completed_at = NOW()
            """;

        jdbcTemplate.update(sql, userId, topicId, questionId);
    }

    // 5.1 Check if question is already completed by user
    public boolean isQuestionCompletedByUser(Integer userId, Integer questionId) {
        String sql = """
            SELECT COUNT(*) FROM vocab_user_progress 
            WHERE user_id = ? AND question_id = ? AND is_completed = 1 AND type = 'exercise'
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, questionId);
        return count != null && count > 0;
    }

    // 6. Update user XP
    public void updateUserXP(Integer userId, Integer xpToAdd) {
        String sql = "UPDATE users SET total_xp = total_xp + ? WHERE id = ?";
        jdbcTemplate.update(sql, xpToAdd, userId);
    }

    // 7. Get user total XP
    public Integer getUserTotalXP(Integer userId) {
        String sql = "SELECT total_xp FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId);
    }

    // 8. Get topic info
    public String getTopicName(Integer topicId) {
        String sql = "SELECT name FROM vocab_topics WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, topicId);
    }

    // 9. Get overall progress
    public OverallProgressDTO getOverallProgress(Integer topicId, Integer userId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT vw.id) as total_words,
                COUNT(DISTINCT CASE 
                    WHEN vup_word.is_completed = 1 
                    THEN vw.id 
                END) as learned_words,
                COUNT(DISTINCT q.id) as total_exercises,
                COUNT(DISTINCT CASE 
                    WHEN vup_ex.is_completed = 1 
                    THEN q.id 
                END) as completed_exercises
            FROM vocab_topics vt
            LEFT JOIN vocab_words vw ON vw.topic_id = vt.id AND vw.is_active = 1
            LEFT JOIN vocab_user_progress vup_word ON 
                vup_word.word_id = vw.id 
                AND vup_word.user_id = ? 
                AND vup_word.type = 'flashcard'
            LEFT JOIN topic_exercise_type tet ON tet.topic_id = vt.id
            LEFT JOIN vocab_exercise_types vet ON vet.id = tet.exercise_type_id AND vet.is_active = 1
            LEFT JOIN vocab_exercise_questions q ON q.type_id = vet.id AND q.topic_id = ? AND q.is_active = 1
            LEFT JOIN vocab_user_progress vup_ex ON 
                vup_ex.question_id = q.id 
                AND vup_ex.user_id = ? 
                AND vup_ex.type = 'exercise'
            WHERE vt.id = ?
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            int totalWords = rs.getInt("total_words");
            int learnedWords = rs.getInt("learned_words");
            int totalExercises = rs.getInt("total_exercises");
            int completedExercises = rs.getInt("completed_exercises");

            int totalItems = totalWords + totalExercises;
            int completedItems = learnedWords + completedExercises;
            int percentage = totalItems > 0 ? (completedItems * 100 / totalItems) : 0;

            Integer totalXp = getUserTotalXP(userId);

            return new OverallProgressDTO(
                    totalWords, learnedWords,
                    totalExercises, completedExercises,
                    totalXp, percentage
            );
        }, userId, topicId, userId, topicId);
    }

    // 10. Get exercise type progress list
    public List<ExerciseTypeProgressDTO> getExerciseTypeProgress(Integer topicId, Integer userId) {
        String sql = """
            SELECT 
                vet.id as type_id,
                vet.name as type_name,
                COUNT(DISTINCT q.id) as total,
                COUNT(DISTINCT CASE 
                    WHEN vup.is_completed = 1 AND vup.topic_id = ?
                    THEN vup.question_id 
                END) as completed
            FROM vocab_exercise_types vet
            INNER JOIN topic_exercise_type tet ON vet.id = tet.exercise_type_id
            LEFT JOIN vocab_exercise_questions q ON vet.id = q.type_id AND q.topic_id = ? AND q.is_active = 1
            LEFT JOIN vocab_user_progress vup ON 
                vup.question_id = q.id 
                AND vup.user_id = ? 
                AND vup.type = 'exercise'
            WHERE tet.topic_id = ? AND vet.is_active = 1
            GROUP BY vet.id, vet.name
            ORDER BY vet.id
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int total = rs.getInt("total");
            int completed = rs.getInt("completed");
            int percentage = total > 0 ? (completed * 100 / total) : 0;

            return new ExerciseTypeProgressDTO(
                    rs.getInt("type_id"),
                    rs.getString("type_name"),
                    total,
                    completed,
                    percentage
            );
        }, topicId, topicId, userId, topicId);
    }

    // 11. Reset progress
    public void resetProgress(Integer typeId, Integer userId) {
        String sql = """
            DELETE FROM vocab_user_progress 
            WHERE user_id = ? 
            AND question_id IN (
                SELECT id FROM vocab_exercise_questions 
                WHERE type_id = ?
            )
            AND type = 'exercise'
            """;

        jdbcTemplate.update(sql, userId, typeId);
    }

    // 12. Get topic IDs by question ID (a question can belong to multiple topics via exercise types)
    // Returns the first topic ID found, or you should pass topicId from client context
    public List<Integer> getTopicIdsByQuestionId(Integer questionId) {
        String sql = """
            SELECT DISTINCT tet.topic_id 
            FROM vocab_exercise_questions q
            INNER JOIN vocab_exercise_types vet ON q.type_id = vet.id
            INNER JOIN topic_exercise_type tet ON vet.id = tet.exercise_type_id
            WHERE q.id = ?
            """;
        return jdbcTemplate.queryForList(sql, Integer.class, questionId);
    }

    // Helper method: Get single topic ID (use first match)
    // NOTE: Ideally, topicId should be passed from client context to avoid ambiguity
    public Integer getTopicIdByQuestionId(Integer questionId) {
        List<Integer> topicIds = getTopicIdsByQuestionId(questionId);
        if (topicIds.isEmpty()) {
            throw new RuntimeException("No topic found for question ID: " + questionId);
        }
        return topicIds.get(0);
    }
}
