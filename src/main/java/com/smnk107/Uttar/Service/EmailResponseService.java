package com.smnk107.Uttar.Service;

import com.smnk107.Uttar.Entity.EmailRequestDto;
import com.smnk107.Uttar.LLMProvider.AIProvider;
import com.smnk107.Uttar.LLMProvider.LLMFactory;
import com.smnk107.Uttar.LLMProvider.OpenRouterLlmProvider;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
//@Log4j
//@AllArgsConstructor
public class EmailResponseService {


    private final WebClient webClient;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final LLMFactory llmFactory;

    private final Map<String, RateLimiter> ipLimiters = new ConcurrentHashMap<>();



    public EmailResponseService(WebClient webClient, RateLimiterRegistry rateLimiterRegistry, LLMFactory llmFactory) {
        this.webClient = webClient;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.llmFactory = llmFactory;
    }

//    @Value("${free.api.key}")
//    private String apiKey;
//
//    @Value("${free.api.url}")
//    private String apiUrl;


    @Retry(name = "emailResponseRetry", fallbackMethod = "emailResponseFallback")
    public String generateEmailResponse(EmailRequestDto emailRequestDto){
        //build the prompt
        String prompt = buildPrompt(emailRequestDto);


        AIProvider aiProvider = llmFactory.getProvider("STUDENT");
        return aiProvider.generateResponse(prompt);


    }

    public String emailResponseFallback(EmailRequestDto emailRequestDto, Throwable throwable){
        return  "Sorry, failed to genearte reply !";
    }

    private String buildPrompt(EmailRequestDto emailRequestDto) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("You are an expert email writer.\n" +
                "\n" +
                "Write a complete email reply.");

        if(emailRequestDto.getTone() != null && !emailRequestDto.getTone().isEmpty()){
            stringBuilder.append("use a ").append(emailRequestDto.getTone()).append(" tone.");
        }

        stringBuilder.append("Rules:\n" +
                "- Do NOT add subject line.\n" +
                "- Do NOT add explanations.\n" +
                "- Do NOT say \"Here is your reply\".\n" +
                "- Output ONLY the email body.\n" +
                "- Make it natural and professional.\n" +
                "- add generic space holder at the place of mail signature\n" +
                "\n" +
                "Original Email: ").append(emailRequestDto.getEmailContent());

        return stringBuilder.toString();
    }

}
