package com.smnk107.Uttar.Service;

import com.smnk107.Uttar.Entity.EmailLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@Service
public class LoggingService {

    @Autowired
    S3Client s3Client;


    @Value("${aws.bucket.name}")
    String bucketName;


    ResponseEntity<String> updateLog(EmailLog emailLog) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(emailLog);

        String key = "logs/email_log_" + System.currentTimeMillis() + ".json";
        System.out.println("Bucket name: " + bucketName);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket("smnk107-email-reply-bucket")
                        .key(key)
                        .contentType("application/json")
                        .build(),
                RequestBody.fromBytes(json.getBytes()));


//        File file = new File("logs/email_log_" + System.currentTimeMillis() + ".json");
//
//        objectMapper.writeValue(file, emailLog);

        return ResponseEntity.ok(key);
    }
    ResponseEntity<String> uploadFile(File file) throws IOException {

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(file.getName())
                        .build(), RequestBody.fromFile(file));

        return ResponseEntity.ok("file uploaded successfully, file name: "+file.getName());

    }
}
