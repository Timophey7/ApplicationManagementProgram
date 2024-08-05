package com.notservice.service.impl;

import com.notservice.models.MessageResponse;
import com.notservice.service.SendMessageService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class SendMessageServiceImpl implements SendMessageService {

    JavaMailSender mailSender;
    MimeMessageHelper helper;

    @Override
    public void sendMessage(MessageResponse messageResponse) {
        try {
            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <title>Приветственное письмо</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <p>Добро пожаловать!</p>\n" +
                    "  <a href=\"[MessageResponse]\">\n" +
                    "    <button>Узнать больше</button>\n" +
                    "  </a>\n" +
                    "</body>\n" +
                    "</html>";

            String url = "http:localhost:8080/apps/"+messageResponse.getApp_id()+"/tasks";
            htmlContent = htmlContent.replace("[MessageResponse]", url);
            helper.setTo(messageResponse.getEmail());
            helper.setFrom("timopheyonisenko@gmail.com");
            helper.setText(htmlContent,true);
            helper.setSubject("invite in app");
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
