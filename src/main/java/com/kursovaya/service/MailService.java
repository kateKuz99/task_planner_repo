package com.kursovaya.service;

public interface MailService {
    void sendSimpleEmail(String to, String subject, String text);
}
