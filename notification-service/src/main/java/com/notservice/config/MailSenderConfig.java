package com.notservice.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Configuration
public class MailSenderConfig {

    @Bean
    public MimeMessage mimeMessage(){
        return mailSender().createMimeMessage();
    }

    @Bean
    public MimeMessageHelper helper(){
        return new MimeMessageHelper(mimeMessage(),"utf-8");
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);
        mailSender.setUsername("hello");
        mailSender.setPassword("hello");
        return mailSender;
    }

}
