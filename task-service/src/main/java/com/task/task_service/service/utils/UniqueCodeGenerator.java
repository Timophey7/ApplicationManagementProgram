package com.task.task_service.service.utils;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class UniqueCodeGenerator {

    public static String generateCode(String appName){
        try {
            String salt = UUID.randomUUID().toString();
            String concated = appName.concat(salt);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] appNameBytes = concated.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(appNameBytes);
            String uniqueCode = Base64.getEncoder().encodeToString(hashBytes);
            return uniqueCode.substring(0,8);
        }catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
