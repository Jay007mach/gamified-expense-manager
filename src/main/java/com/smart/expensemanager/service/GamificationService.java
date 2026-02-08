package com.smart.expensemanager.service;

import com.smart.expensemanager.model.*;
import com.smart.expensemanager.repository.*;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class GamificationService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserStatsRepository userStatsRepository;
    private final PersonalExpenseService expenseService;

    // Default achievements
    private final List<Achievement> defaultAchievements = Arrays.asList(
            new Achievement("First Step", "Add your first expense", Achievement.AchievementType.EXPENSE_EXPLORER, 1, 10),
            new Achievement("Consistent Tracker", "Track expenses for 7 consecutive days", Achievement.AchievementType.CONSISTENT_TRACKER, 7, 50),
            new Achievement("Weekly Warrior", "Track expenses for 30 consecutive days", Achievement.AchievementType.WEEKLY_WARRIOR, 30, 100),
            new Achievement("Frugal Day", "Have a day with no expenses", Achievement.AchievementType.NO_SPEND_DAY, 1, 25),
            new Achievement("Savings Streak", "Have 5 no-spend days in a month", Achievement.AchievementType.SAVINGS_STREAK, 5, 75),
            new Achievement("Category Master", "Use all expense categories at least once", Achievement.AchievementType.CATEGORY_SAVER, 7, 60),
            new Achievement("Early Bird", "Track an expense before 12 PM", Achievement.AchievementType.EARLY_BIRD, 5, 40),
            new Achievement("Budget Champion", "Stay under budget for a month", Achievement.AchievementType.BUDGET_CHAMPION, 1, 150)
    );

    public GamificationService(AchievementRepository achievementRepository,
                               UserAchievementRepository userAchievementRepository,
                               UserStatsRepository userStatsRepository,
                               PersonalExpenseService expenseService) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userStatsRepository = userStatsRepository;
        this.expenseService = expenseService;
    }

    public void initializeGamification() {
        // Create default achievements if they don't exist
        if (achievementRepository.count() == 0) {
            achievementRepository.saveAll(defaultAchievements);
        }

        // Initialize user stats if they don't exist
        if (userStatsRepository.count() == 0) {
            userStatsRepository.save(new UserStats());
        }

        // Initialize user achievements if they don't exist
        if (userAchievementRepository.count() == 0) {
            List<Achievement> allAchievements = achievementRepository.findByIsActiveTrue();
            List<UserAchievement> userAchievements = new ArrayList<>();

            for (Achievement achievement : allAchievements) {
                userAchievements.add(new UserAchievement(achievement));
            }

            userAchievementRepository.saveAll(userAchievements);
        }
    }

    // Event listener for expense addition
    @EventListener
    public void handleExpenseAdded(PersonalExpenseService.ExpenseAddedEvent event) {
        UserStats stats = getUserStats();
        updateConsecutiveDays(stats);
        checkExpenseBasedAchievements(event.getExpense(), stats);
        userStatsRepository.save(stats);
    }

    // Daily check using scheduler
    @Scheduled(cron = "0 0 9 * * ?") // Run daily at 9 AM
    public void onDailyCheck() {
        UserStats stats = getUserStats();
        updateConsecutiveDays(stats);
        checkNoSpendDayAchievements(stats);
        checkConsistencyAchievements(stats);
        userStatsRepository.save(stats);
    }

    private UserStats getUserStats() {
        return userStatsRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> {
                    UserStats newStats = new UserStats();
                    return userStatsRepository.save(newStats);
                });
    }

    private void updateConsecutiveDays(UserStats stats) {
        LocalDate today = LocalDate.now();
        LocalDate lastActivity = stats.getLastActivityDate();

        if (lastActivity == null) {
            stats.resetConsecutiveDays();
        } else if (lastActivity.equals(today)) {
            // Already updated today
            return;
        } else if (lastActivity.plusDays(1).equals(today)) {
            // Consecutive day
            stats.incrementConsecutiveDays();
        } else {
            // Break in streak
            stats.resetConsecutiveDays();
        }
    }

    private void checkExpenseBasedAchievements(PersonalExpense expense, UserStats stats) {
        checkFirstExpenseAchievement();
        checkCategoryMasterAchievement();
        checkEarlyBirdAchievement(expense);
    }

    private void checkFirstExpenseAchievement() {
        Long totalExpenses = (long) expenseService.listAll().size();
        if (totalExpenses == 1) {
            unlockAchievement(Achievement.AchievementType.EXPENSE_EXPLORER, 1);
        }
    }

    private void checkCategoryMasterAchievement() {
        List<PersonalExpense> allExpenses = expenseService.listAll();
        long uniqueCategories = allExpenses.stream()
                .map(PersonalExpense::getCategory)
                .distinct()
                .count();

        UserAchievement categoryAchievement = userAchievementRepository.findByAchievementId(
                getAchievementIdByType(Achievement.AchievementType.CATEGORY_SAVER)
        ).orElse(null);

        if (categoryAchievement != null && !categoryAchievement.getIsUnlocked()) {
            categoryAchievement.setCurrentProgress((int) uniqueCategories);
            userAchievementRepository.save(categoryAchievement);

            if (uniqueCategories >= 7) { // All 7 categories
                unlockAchievement(Achievement.AchievementType.CATEGORY_SAVER, 75);
            }
        }
    }

    private void checkEarlyBirdAchievement(PersonalExpense expense) {
        if (expense.getDate().equals(LocalDate.now())) {
            // If expense was added today and it's before 12 PM
            if (java.time.LocalTime.now().getHour() < 12) {
                UserAchievement earlyBirdAchievement = userAchievementRepository.findByAchievementId(
                        getAchievementIdByType(Achievement.AchievementType.EARLY_BIRD)
                ).orElse(null);

                if (earlyBirdAchievement != null && !earlyBirdAchievement.getIsUnlocked()) {
                    earlyBirdAchievement.incrementProgress();
                    userAchievementRepository.save(earlyBirdAchievement);
                }
            }
        }
    }

    private void checkNoSpendDayAchievements(UserStats stats) {
        LocalDate today = LocalDate.now();
        List<PersonalExpense> todayExpenses = expenseService.listAll().stream()
                .filter(expense -> expense.getDate().equals(today))
                .toList();

        if (todayExpenses.isEmpty()) {
            // No expenses today - it's a no-spend day!
            stats.incrementNoSpendDays();

            // Unlock single no-spend day achievement
            unlockAchievement(Achievement.AchievementType.NO_SPEND_DAY, 1);

            // Check for savings streak (5 no-spend days in a month)
            if (stats.getNoSpendDaysThisMonth() >= 5) {
                unlockAchievement(Achievement.AchievementType.SAVINGS_STREAK, 1);
            }
        }
    }

    private void checkConsistencyAchievements(UserStats stats) {
        int consecutiveDays = stats.getConsecutiveTrackingDays();

        if (consecutiveDays >= 7) {
            unlockAchievement(Achievement.AchievementType.CONSISTENT_TRACKER, 1);
        }

        if (consecutiveDays >= 30) {
            unlockAchievement(Achievement.AchievementType.WEEKLY_WARRIOR, 1);
        }
    }

    private void unlockAchievement(Achievement.AchievementType type, Integer progressToAdd) {
        UserAchievement userAchievement = userAchievementRepository.findByAchievementId(
                getAchievementIdByType(type)
        ).orElse(null);

        if (userAchievement != null && !userAchievement.getIsUnlocked()) {
            if (progressToAdd > 0) {
                userAchievement.addProgress(progressToAdd);
            }

            if (userAchievement.getIsUnlocked()) {
                UserStats stats = getUserStats();
                stats.addPoints(userAchievement.getAchievement().getPointsReward());
                stats.incrementAchievementsUnlocked();
                userStatsRepository.save(stats);
            }

            userAchievementRepository.save(userAchievement);
        }
    }

    private Long getAchievementIdByType(Achievement.AchievementType type) {
        return achievementRepository.findByType(type).stream()
                .findFirst()
                .map(Achievement::getId)
                .orElse(0L);
    }

    // Get methods for controllers
    public UserStats getUserStatsWithLevel() {
        return getUserStats();
    }

    public List<UserAchievement> getUserAchievements() {
        return userAchievementRepository.findAll();
    }

    public List<UserAchievement> getUnlockedAchievements() {
        return userAchievementRepository.findByIsUnlockedTrue();
    }

    public List<UserAchievement> getLockedAchievements() {
        return userAchievementRepository.findByIsUnlockedFalse();
    }

    public Integer getTotalPoints() {
        return getUserStats().getTotalPoints();
    }

    public Integer getUnlockedAchievementsCount() {
        return userAchievementRepository.countByIsUnlockedTrue();
    }

    public Integer getTotalAchievementsCount() {
        return (int) userAchievementRepository.count();
    }
}