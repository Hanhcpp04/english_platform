package com.back_end.english_app.service.user;

import com.back_end.english_app.config.GeminiConfig;
import com.back_end.english_app.dto.respones.writing.GradingResultResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Grade writing using Gemini AI
     */
    public GradingResultResponse gradeWriting(String writingContent, String taskQuestion) {
        try {
            log.info("Calling Gemini API to grade writing");
            
            // Build prompt
            String prompt = buildGradingPrompt(writingContent, taskQuestion);
            
            // Call Gemini API
            String geminiResponse = callGeminiAPI(prompt);
            
            // Parse response
            return parseGradingResponse(geminiResponse, writingContent);
            
        } catch (Exception e) {
            log.error("Error grading writing with Gemini: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to grade writing: " + e.getMessage());
        }
    }

    /**
     * Build grading prompt for Gemini
     */
    private String buildGradingPrompt(String writingContent, String taskQuestion) {
        return String.format("""
            You are an expert English writing evaluator. Please grade the following essay and provide detailed feedback.
            
            TASK/PROMPT:
            %s
            
            STUDENT'S WRITING:
            %s
            
            Please provide your evaluation in the following JSON format:
            {
                "grammarScore": <0-100>,
                "vocabularyScore": <0-100>,
                "coherenceScore": <0-100>,
                "overallScore": <0-100>,
                "generalFeedback": "<overall feedback about the writing>",
                "grammarSuggestions": [
                    {
                        "error": "<grammatical error found>",
                        "suggestion": "<correct version>",
                        "explanation": "<why this is wrong>"
                    }
                ],
                "vocabularySuggestions": [
                    {
                        "word": "<basic/overused word>",
                        "betterAlternative": "<advanced synonym>",
                        "reason": "<why the alternative is better>"
                    }
                ]
            }
            
            Grading Criteria:
            - Grammar Score: Check for grammar mistakes, sentence structure, tenses
            - Vocabulary Score: Evaluate word choice, variety, and appropriateness
            - Coherence Score: Assess logical flow, organization, and clarity
            - Overall Score: Average of all scores, considering relevance to the task
            
            Provide constructive and encouraging feedback. Return ONLY the JSON, no additional text.
            """, taskQuestion, writingContent);
    }

    /**
     * Call Gemini API
     */
    private String callGeminiAPI(String prompt) throws Exception {
        String url = geminiConfig.getApiUrl() + "?key=" + geminiConfig.getApiKey();
        
        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        parts.add(part);
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);
        
        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", geminiConfig.getTemperature());
        generationConfig.put("maxOutputTokens", geminiConfig.getMaxTokens());
        requestBody.put("generationConfig", generationConfig);
        
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // Call API
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            entity,
            String.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK) {
            return extractTextFromGeminiResponse(response.getBody());
        } else {
            throw new RuntimeException("Gemini API returned status: " + response.getStatusCode());
        }
    }

    /**
     * Extract text from Gemini API response
     */
    private String extractTextFromGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode candidates = root.path("candidates");
        
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.path("content");
            JsonNode parts = content.path("parts");
            
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }
        
        throw new RuntimeException("Invalid response format from Gemini API");
    }

    /**
     * Parse Gemini response to GradingResultResponse
     */
    private GradingResultResponse parseGradingResponse(String geminiResponse, String originalContent) throws Exception {
        // Clean response (remove markdown code blocks if present)
        String cleanedResponse = geminiResponse
            .replaceAll("```json\\s*", "")
            .replaceAll("```\\s*", "")
            .trim();
        
        log.debug("Cleaned Gemini response for parsing: {}", cleanedResponse.substring(0, Math.min(500, cleanedResponse.length())));
        
        JsonNode gradingJson;
        try {
            gradingJson = objectMapper.readTree(cleanedResponse);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Failed to parse Gemini response. Response length: {}, First 500 chars: {}", 
                cleanedResponse.length(), 
                cleanedResponse.substring(0, Math.min(500, cleanedResponse.length())));
            throw new RuntimeException("Invalid JSON response from Gemini AI. The response may be truncated or malformed.", e);
        }
        
        // Parse scores
        Integer grammarScore = gradingJson.path("grammarScore").asInt(0);
        Integer vocabularyScore = gradingJson.path("vocabularyScore").asInt(0);
        Integer coherenceScore = gradingJson.path("coherenceScore").asInt(0);
        Integer overallScore = gradingJson.path("overallScore").asInt(0);
        
        // Parse feedback
        String generalFeedback = gradingJson.path("generalFeedback").asText("");
        
        // Parse grammar suggestions
        List<GradingResultResponse.GrammarSuggestion> grammarSuggestions = new ArrayList<>();
        JsonNode grammarNode = gradingJson.path("grammarSuggestions");
        if (grammarNode.isArray()) {
            for (JsonNode node : grammarNode) {
                grammarSuggestions.add(GradingResultResponse.GrammarSuggestion.builder()
                    .error(node.path("error").asText())
                    .suggestion(node.path("suggestion").asText())
                    .explanation(node.path("explanation").asText())
                    .build());
            }
        }
        
        // Parse vocabulary suggestions
        List<GradingResultResponse.VocabularySuggestion> vocabularySuggestions = new ArrayList<>();
        JsonNode vocabNode = gradingJson.path("vocabularySuggestions");
        if (vocabNode.isArray()) {
            for (JsonNode node : vocabNode) {
                vocabularySuggestions.add(GradingResultResponse.VocabularySuggestion.builder()
                    .word(node.path("word").asText())
                    .betterAlternative(node.path("betterAlternative").asText())
                    .reason(node.path("reason").asText())
                    .build());
            }
        }
        
        // Count words
        int wordCount = originalContent.split("\\s+").length;
        
        return GradingResultResponse.builder()
            .grammarScore(grammarScore)
            .vocabularyScore(vocabularyScore)
            .coherenceScore(coherenceScore)
            .overallScore(overallScore)
            .generalFeedback(generalFeedback)
            .grammarSuggestions(grammarSuggestions)
            .vocabularySuggestions(vocabularySuggestions)
            .wordCount(wordCount)
            .build();
    }
}
