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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SendMessageServiceImpl implements SendMessageService {

    JavaMailSender mailSender;
    MimeMessageHelper helper;

    @Override
    public void sendMessage(MessageResponse messageResponse) {
        try {
            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Приветственное письмо</title>
                    </head>
                    <body>
                        <p>Добро пожаловать!</p>
                        <a href=[MessageResponse]>
                            <button>Узнать больше</button>
                        </a>
                    </body>
                    </html>
                    """;

            String url = "http//:localhost:8080/apps/" + messageResponse.getUniqueCode() + "/tasks";
            htmlContent = htmlContent.replace("[MessageResponse]", url);
            helper.setTo(messageResponse.getEmail());
            helper.setFrom("timopheyonisenko@gmail.com");
            helper.setText(htmlContent, true);
            helper.setSubject("invite in app");
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
