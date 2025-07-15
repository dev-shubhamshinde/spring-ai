package com.springai_iai_genai.springai_iai_genai.controller;

import com.springai_iai_genai.springai_iai_genai.service.AskAnythingService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Controller
public class ServiceController {
    @Autowired
    private AskAnythingService askAnythingService;

    @GetMapping("/askAnything")
    public String askAnything() {
        return "askAnything";
    }

    @PostMapping(value = "/askAnythingStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> askAnythingStream(@RequestBody String question) {
        return askAnythingService.streamAnswer(question);
    }
}
