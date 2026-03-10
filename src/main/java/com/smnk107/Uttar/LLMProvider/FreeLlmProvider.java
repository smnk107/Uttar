package com.smnk107.Uttar.LLMProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service("freeLlmProvider")
@RequiredArgsConstructor
public class FreeLlmProvider implements AIProvider{

    private final WebClient webClient;

    @Value("${free.api.url}")
    private String apiUrl;

    @Value("${free.api.key}")
    private String apiKey;

    @Override
    public String generateResponse(String prompt) {

        Map<String, String> body = Map.of(
                "message", prompt
        );

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
