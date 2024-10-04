package com.example.task_back.controller;

import com.example.task_back.service.AiService;
import com.example.task_back.service.PrayerService;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AssistantController {

    private final AiService aiService;

    @Autowired
    public AssistantController(AiService aiService){
        this.aiService = aiService;
    }

    @GetMapping("/generatePrayer")
    public ResponseEntity<String> generatePrayer(@RequestParam(value = "message", defaultValue = "간단한 기도문 작성해줘") String message) {
        return ResponseEntity.ok(aiService.generatePrayer(message));
    }

    @GetMapping("/analysis")
    public ResponseEntity<Map<String,Integer>> generateStream(@RequestParam(value = "message", defaultValue = "") String message) {
        return ResponseEntity.ok(aiService.analysisPrayer());
    }
    
}
