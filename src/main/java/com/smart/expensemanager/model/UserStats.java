package com.smart.expensemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalPoints;
    private Integer currentLevel;
    private Integer consecutiveTrackingDays;
    private Integer noSpendDaysThisMonth;
    private Integer totalAchievementsUnlocked;
    private LocalDate lastActivityDate;
    private LocalDate lastNoSpendDay;

    public UserStats() {
        this.totalPoints = 0;
        this.currentLevel = 1;
        this.consecutiveTrackingDays = 0;
        this.noSpendDaysThisMonth = 0;
        this.totalAchievementsUnlocked = 0;
        this.lastActivityDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }

    public Integer getConsecutiveTrackingDays() { return consecutiveTrackingDays; }
    public void setConsecutiveTrackingDays(Integer consecutiveTrackingDays) { this.consecutiveTrackingDays = consecutiveTrackingDays; }

    public Integer getNoSpendDaysThisMonth() { return noSpendDaysThisMonth; }
    public void setNoSpendDaysThisMonth(Integer noSpendDaysThisMonth) { this.noSpendDaysThisMonth = noSpendDaysThisMonth; }

    public Integer getTotalAchievementsUnlocked() { return totalAchievementsUnlocked; }
    public void setTotalAchievementsUnlocked(Integer totalAchievementsUnlocked) { this.totalAchievementsUnlocked = totalAchievementsUnlocked; }

    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public LocalDate getLastNoSpendDay() { return lastNoSpendDay; }
    public void setLastNoSpendDay(LocalDate lastNoSpendDay) { this.lastNoSpendDay = lastNoSpendDay; }

    public void addPoints(Integer points) {
        this.totalPoints += points;
        updateLevel();
    }

    public void incrementConsecutiveDays() {
        this.consecutiveTrackingDays++;
        this.lastActivityDate = LocalDate.now();
    }

    public void resetConsecutiveDays() {
        this.consecutiveTrackingDays = 1;
        this.lastActivityDate = LocalDate.now();
    }

    public void incrementNoSpendDays() {
        this.noSpendDaysThisMonth++;
        this.lastNoSpendDay = LocalDate.now();
    }

    public void incrementAchievementsUnlocked() {
        this.totalAchievementsUnlocked++;
    }

    private void updateLevel() {
        this.currentLevel = Math.min(50, 1 + (this.totalPoints / 100));
    }

    public Integer getPointsToNextLevel() {
        return Math.max(0, (this.currentLevel * 100) - this.totalPoints);
    }

    public Integer getLevelProgressPercentage() {
        int pointsInCurrentLevel = this.totalPoints - ((this.currentLevel - 1) * 100);
        return Math.min(100, (pointsInCurrentLevel * 100) / 100);
    }
}