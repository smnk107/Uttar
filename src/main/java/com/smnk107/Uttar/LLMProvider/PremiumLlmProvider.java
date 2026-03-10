package com.smnk107.Uttar.LLMProvider;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service("premiumLlmProvider")
@RequiredArgsConstructor
public class PremiumLlmProvider implements AIProvider{
    private final WebClient webClient;

    @Value("${premium.api.url}")
    private String apiUrl;

    @Value("${premium.api.key}")
    private String apiKey;

    @Override
    public String generateResponse(String prompt) {

        Map<String, Object> body = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.path("response").asText())
                .block();
    }
}
