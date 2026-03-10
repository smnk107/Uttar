package com.smnk107.Uttar.LLMProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service("openRouterLlmProvider")
@RequiredArgsConstructor
public class OpenRouterLlmProvider implements AIProvider {

    private final WebClient webClient;

    @Value("${openrouter.api.key}")
    private String apiKey;
    @Value("${openrouter.api.url}")
    private String apiurl;

    @Override
    public String generateResponse(String prompt) {



        Map<String, Object> requestBody = Map.of(
                "model", "meta-llama/llama-3-8b-instruct",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .uri(apiurl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json
                        .get("choices")
                        .get(0)
                        .get("message")
                        .get("content")
                        .asText())
                .block(); // since your service is sync
    }
}

