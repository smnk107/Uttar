package com.smnk107.Uttar.LLMProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LLMFactory {

    private final Map<String, AIProvider> providers;

    public AIProvider getProvider(String subscriptionType) {

        if ("PREMIUM".equalsIgnoreCase(subscriptionType)) {
            return providers.get("premiumLlmProvider");
        }
        else if ("FREE".equalsIgnoreCase(subscriptionType)){
            return providers.get("freeLlmProvider");
        }

        return providers.get("openRouterLlmProvider");
    }
}

