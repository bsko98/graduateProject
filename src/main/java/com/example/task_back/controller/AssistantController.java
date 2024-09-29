package com.example.task_back.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AssistantController {
    private final OpenAiChatModel chatModel;

    @Autowired
    public AssistantController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "간단한 기도문 작성해줘") String message) {
        return chatModel.call(message);
    }

    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatModel.stream(prompt);
    }
    
}
