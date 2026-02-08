package com.smart.expensemanager.controller;

import com.smart.expensemanager.model.UserAchievement;
import com.smart.expensemanager.model.UserStats;
import com.smart.expensemanager.service.GamificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/gamification")
public class GamificationController {

    private final GamificationService gamificationService;

    public GamificationController(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @GetMapping("/achievements")
    public String showAchievements(Model model) {
        List<UserAchievement> allAchievements = gamificationService.getUserAchievements();
        List<UserAchievement> unlockedAchievements = gamificationService.getUnlockedAchievements();
        List<UserAchievement> lockedAchievements = gamificationService.getLockedAchievements();
        UserStats userStats = gamificationService.getUserStatsWithLevel();

        model.addAttribute("allAchievements", allAchievements);
        model.addAttribute("unlockedAchievements", unlockedAchievements);
        model.addAttribute("lockedAchievements", lockedAchievements);
        model.addAttribute("userStats", userStats);
        model.addAttribute("totalPoints", gamificationService.getTotalPoints());
        model.addAttribute("unlockedCount", gamificationService.getUnlockedAchievementsCount());
        model.addAttribute("totalCount", gamificationService.getTotalAchievementsCount());

        return "gamification/achievements";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        UserStats userStats = gamificationService.getUserStatsWithLevel();
        model.addAttribute("userStats", userStats);
        model.addAttribute("levelProgress", userStats.getLevelProgressPercentage());
        model.addAttribute("pointsToNextLevel", userStats.getPointsToNextLevel());

        return "gamification/leaderboard";
    }
}