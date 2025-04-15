package com.kursovaya.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    private final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("fern@miniuser.ru");
        logger.info("Send message to - "+to);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String code) {
        try {
            String template = new String(Files.readAllBytes(Paths.get("backend/src/main/java/com/kursovaya/dto/html/verification-template.html")));

            String filledTemplate = fillCodeInTemplate(template, code);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("fern@miniuser.ru");
            helper.setText(filledTemplate, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String fillCodeInTemplate(String template, String code) {
        char[] codeArray = code.toCharArray();

        for (int i = 0; i < codeArray.length; i++) {
            template = template.replace("${code[" + i + "]}", String.valueOf(codeArray[i]));
        }
        return template;
    }
}
