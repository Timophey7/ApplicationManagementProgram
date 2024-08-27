package com.notservice.service.impl;

import com.notservice.models.MessageResponse;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendMessageServiceImplTest {

    @InjectMocks
    SendMessageServiceImpl sendMessageService;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MimeMessageHelper helper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void sendMessage() throws MessagingException {
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setEmail("test@gmail.com");
        messageResponse.setUniqueCode("uniqueCode");

        sendMessageService.sendMessage(messageResponse);

        verify(helper).setTo(messageResponse.getEmail());
        verify(helper).setFrom("timopheyonisenko@gmail.com");
        verify(helper).setSubject("invite in app");

    }
}