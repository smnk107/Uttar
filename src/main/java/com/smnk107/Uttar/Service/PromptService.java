package com.smnk107.Uttar.Service;

import com.smnk107.Uttar.Entity.EmailRequestDto;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    String buildPrompt(EmailRequestDto emailRequestDto) {

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
