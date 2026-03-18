package com.smnk107.Uttar.Service;

import com.smnk107.Uttar.Entity.EmailLog;
import com.smnk107.Uttar.Entity.EmailRequestDto;
import com.smnk107.Uttar.Entity.UsageLog;
import com.smnk107.Uttar.LLMProvider.AIProvider;
import com.smnk107.Uttar.LLMProvider.LLMFactory;
import com.smnk107.Uttar.LLMProvider.OpenRouterLlmProvider;
import com.smnk107.Uttar.Repository.UsageLogRepo;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
//@Log4j
//@AllArgsConstructor
public class EmailResponseService {


    private final WebClient webClient;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final LLMFactory llmFactory;
    private final PromptService promptService;
    private final LoggingService loggingService;
    private final UsageLogRepo usageLogRepo;

    private final Map<String, RateLimiter> ipLimiters = new ConcurrentHashMap<>();



    public EmailResponseService(WebClient webClient, RateLimiterRegistry rateLimiterRegistry, LLMFactory llmFactory,PromptService promptService, LoggingService loggingService, UsageLogRepo usageLogRepo) {
        this.webClient = webClient;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.llmFactory = llmFactory;
        this.promptService = promptService;
        this.loggingService = loggingService;
        this.usageLogRepo = usageLogRepo;
    }

//    @Value("${free.api.key}")
//    private String apiKey;
//
//    @Value("${free.api.url}")
//    private String apiUrl;


    @Retry(name = "emailResponseRetry", fallbackMethod = "emailResponseFallback")
    public String generateEmailResponse(EmailRequestDto emailRequestDto)  {

        String userEmail = emailRequestDto.getUserEmail();

        String ipAddress = emailRequestDto.getIpAddress();

        String tone = emailRequestDto.getTone();

        String s3LogKey = null;

        String status = "SUCCESS";
        String reply = null;

        //build the prompt
        try{
            String prompt = promptService.buildPrompt(emailRequestDto);


            AIProvider aiProvider = llmFactory.getProvider("STUDENT");
            reply = aiProvider.generateResponse(prompt);
        }catch(Exception e){
            System.out.println("Failed to generate reply !");
            status = "FAILED";
        }

        EmailLog emailLog = EmailLog.builder()
                .emailContent(emailRequestDto.getEmailContent())
                .createdAt(LocalDateTime.now())
                .generatedReply(reply)
                .tone(tone)
                .build();

        try {
            s3LogKey = loggingService.updateLog(emailLog).getBody();
        }
        catch (Exception e){
            System.out.println("Failed to save logs !");
        }

        UsageLog usageLog = UsageLog
                .builder()
                .createdAt(LocalDateTime.now())
                .userEmail(userEmail)
                .status(status)
                .s3LogKey(s3LogKey)
                .tone(tone)
                .ipAddress(ipAddress)
                .build();

        try{
            usageLogRepo.save(usageLog);
        }catch (Exception e){
            System.out.println("Failed to save usage log");
        }

        return reply;
    }

    public String emailResponseFallback(EmailRequestDto emailRequestDto, Throwable throwable) {

        String reply= "Sorry, failed to genearte reply !";

        EmailLog emailLog = EmailLog.builder()
                .emailContent(emailRequestDto.getEmailContent())
                .createdAt(LocalDateTime.now())
                .generatedReply(reply)
                .build();

        try {
            loggingService.updateLog(emailLog);
        }
        catch (Exception e){
            System.out.println("Failed to save logs !");
        }

        return reply;
    }

}
