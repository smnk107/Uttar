package com.smnk107.Uttar.Controller;

import com.smnk107.Uttar.Entity.EmailRequestDto;
import com.smnk107.Uttar.Service.EmailResponseService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailResponseController {


    private final EmailResponseService emailResponseService;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final Map<String, RateLimiter> ipLimiters = new ConcurrentHashMap<>();


    @GetMapping("/hi")
    ResponseEntity<String> hiHello(){

        return ResponseEntity.ok("Hello, Dear User !");
    }

    @PostMapping("/response")
    ResponseEntity<String> responseEmail(@RequestBody EmailRequestDto emailRequestDto, HttpServletRequest servletRequest){

        String clientIp = servletRequest.getRemoteAddr();

        RateLimiter limiter = ipLimiters.computeIfAbsent(clientIp,
                ip -> rateLimiterRegistry.rateLimiter("emailLimiter-" + ip));

        if (!limiter.acquirePermission()) {
            return ResponseEntity.status(429)
                    .body("Too many requests from this IP. Try again later.");
        }

        return ResponseEntity.ok(emailResponseService.generateEmailResponse(emailRequestDto));
    }
}
