package com.task.task_service.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class UniqueCodeGenerator {

    public static String generateCode(String appName){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] appNameBytes = appName.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(appNameBytes);
            String uniqueCode = Base64.getEncoder().encodeToString(hashBytes);
            return uniqueCode.substring(0,8);
        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
