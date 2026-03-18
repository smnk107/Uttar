package com.smnk107.Uttar.Entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmailLog {
    public String emailContent;
    public String tone = "Professional";
    public String generatedReply;
    public LocalDateTime createdAt;
}
