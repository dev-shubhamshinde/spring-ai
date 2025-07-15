package com.springai_iai_genai.springai_iai_genai.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AskAnythingService {

    private final WebClient webClient;

    public AskAnythingService() {
        this.webClient = WebClient.create("http://localhost:11434");
    }

    public Flux<String> streamAnswer(String prompt) {
        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_NDJSON) // Ollama streams NDJSON
                .bodyValue("{\"model\": \"gemma:2b\", \"prompt\": \"" + prompt + "\", \"stream\": true}")
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> {
                    try {
                        // Each line is JSON: {"response":"text", ...}
                        chunk = chunk.trim();
                        if (chunk.isEmpty()) return "";
                        org.json.JSONObject obj = new org.json.JSONObject(chunk);
                        return obj.optString("response", "");
                    } catch (Exception e) {
                        return ""; // silently skip malformed chunks
                    }
                })
                .filter(s -> !s.isEmpty())
                .delayElements(java.time.Duration.ofMillis(50)); // throttle to avoid heating up browser
    }

}
