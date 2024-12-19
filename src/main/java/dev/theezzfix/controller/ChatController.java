package dev.theezzfix.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")    
public class ChatController {

    @Value("${huggingface.api.key}")
    private String apiKey;  

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<Map<String, String>> getAIResponse(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        
        String botMessage = getGPT2Response(userMessage);
  
        Map<String, String> response = new HashMap<>();
        response.put("reply", botMessage);

        return ResponseEntity.ok(response);
    }

    private String getGPT2Response(String message) {
        String apiUrl = "https://api-inference.huggingface.co/models/gpt2";
   
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
  
        String requestBody = String.format("{\"inputs\": \"%s\"}", message);
        
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
      
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return truncateResponse(response.getBody());
        } else {
            return "Error: Unable to process the request.";
        }
    }

    private String truncateResponse(String responseBody){
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            String generatedText = root.path(0).path("generated_text").asText();

            String[] words = generatedText.split("\\s+");
            if (words.length > 20) {
                return String.join(" ", Arrays.copyOfRange(words, 0, 20)) + "...";
            }
            return generatedText;
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn't process the response.";
        }
    };
}