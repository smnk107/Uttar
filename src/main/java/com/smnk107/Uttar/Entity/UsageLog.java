package com.smnk107.Uttar.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class UsageLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String ipAddress;

    private String tone;

    private String s3LogKey;

    private String status; // SUCCESS / FAILED

    private LocalDateTime createdAt;
}
