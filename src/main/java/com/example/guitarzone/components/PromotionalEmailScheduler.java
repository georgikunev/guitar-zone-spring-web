package com.example.guitarzone.components;

import com.example.guitarzone.model.entities.User;
import com.example.guitarzone.service.EmailService;
import com.example.guitarzone.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromotionalEmailScheduler {

    private final EmailService emailService;
    private final UserService userService;

    public PromotionalEmailScheduler(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @Scheduled(cron = "@weekly")
    public void sendPromotionalEmails() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            String subject = "Exclusive Promotions Just for You!";
            String text = "Hello " + user.getFullName() + ",\n\nCheck out our latest promotions on guitars!";
            emailService.sendEmail(user.getEmail(), subject, text);
        }
    }
}
