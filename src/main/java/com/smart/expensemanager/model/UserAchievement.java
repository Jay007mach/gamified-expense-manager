package com.smart.expensemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_achievements")
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    private Integer currentProgress;
    private Boolean isUnlocked;
    private LocalDate unlockedDate;
    private LocalDate lastUpdated;

    public UserAchievement() {}

    public UserAchievement(Achievement achievement) {
        this.achievement = achievement;
        this.currentProgress = 0;
        this.isUnlocked = false;
        this.lastUpdated = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Achievement getAchievement() { return achievement; }
    public void setAchievement(Achievement achievement) { this.achievement = achievement; }

    public Integer getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
        this.lastUpdated = LocalDate.now();
    }

    public Boolean getIsUnlocked() { return isUnlocked; }
    public void setIsUnlocked(Boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
        if (isUnlocked) {
            this.unlockedDate = LocalDate.now();
        }
    }

    public LocalDate getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDate unlockedDate) { this.unlockedDate = unlockedDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    public void incrementProgress() {
        this.currentProgress++;
        this.lastUpdated = LocalDate.now();
        checkIfUnlocked();
    }

    public void addProgress(int amount) {
        this.currentProgress += amount;
        this.lastUpdated = LocalDate.now();
        checkIfUnlocked();
    }

    private void checkIfUnlocked() {
        if (!this.isUnlocked && this.currentProgress >= this.achievement.getTargetValue()) {
            this.isUnlocked = true;
            this.unlockedDate = LocalDate.now();
        }
    }

    public Integer getRemainingProgress() {
        return Math.max(0, this.achievement.getTargetValue() - this.currentProgress);
    }

    public Integer getProgressPercentage() {
        if (this.achievement.getTargetValue() == 0) return 100;
        return (int) ((this.currentProgress * 100.0) / this.achievement.getTargetValue());
    }
}