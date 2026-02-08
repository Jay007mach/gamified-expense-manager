package com.smart.expensemanager;

import com.smart.expensemanager.service.GamificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Add this annotation
public class ExpenseManagerApplication implements CommandLineRunner {

    private final GamificationService gamificationService;

    public ExpenseManagerApplication(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize gamification system on startup
        gamificationService.initializeGamification();
    }
}