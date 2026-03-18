package com.smnk107.Uttar.Entity;

import lombok.Data;

@Data
public class EmailRequestDto {

    private String emailContent;
    private String tone;
    private String userEmail;
    private String ipAddress;
}
