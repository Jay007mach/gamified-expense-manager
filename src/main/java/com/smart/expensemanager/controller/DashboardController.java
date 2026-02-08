package com.smart.expensemanager.controller;

import com.smart.expensemanager.service.PersonalExpenseService;
import com.smart.expensemanager.service.GamificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final PersonalExpenseService personalService;
    private final GamificationService gamificationService;

    public DashboardController(PersonalExpenseService personalService,
                               GamificationService gamificationService) {
        this.personalService = personalService;
        this.gamificationService = gamificationService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalExpenses", personalService.listAll().size());
        model.addAttribute("categorySums", personalService.sumByCategoryCurrentMonth());
        model.addAttribute("monthTotal", personalService.totalForCurrentMonth());
        model.addAttribute("weeklyTotal", personalService.totalForCurrentWeek());
        model.addAttribute("todayTotal", personalService.totalForToday());
        model.addAttribute("monthlyTrends", personalService.getLastSixMonthsTrend());

        model.addAttribute("userStats", gamificationService.getUserStatsWithLevel());
        model.addAttribute("unlockedAchievementsCount", gamificationService.getUnlockedAchievementsCount());
        model.addAttribute("totalPoints", gamificationService.getTotalPoints());

        return "dashboard";
    }
}