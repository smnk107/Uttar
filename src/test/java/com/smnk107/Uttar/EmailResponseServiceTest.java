package com.smnk107.Uttar;

import com.smnk107.Uttar.LLMProvider.LLMFactory;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.web.reactive.function.client.WebClient;

public class EmailResponseServiceTest {

    private  WebClient webClient;
    private  RateLimiterRegistry rateLimiterRegistry;
    private  LLMFactory llmFactory;



}
