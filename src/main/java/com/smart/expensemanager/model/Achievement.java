package com.smart.expensemanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String badgeIcon;

    @Enumerated(EnumType.STRING)
    private AchievementType type;

    private Integer targetValue;
    private Integer pointsReward;
    private Boolean isActive;

    public enum AchievementType {
        SAVINGS_STREAK, NO_SPEND_DAY, BUDGET_CHAMPION,
        CATEGORY_SAVER, WEEKLY_WARRIOR, EARLY_BIRD,
        CONSISTENT_TRACKER, EXPENSE_EXPLORER
    }

    public Achievement() {}

    public Achievement(String name, String description, AchievementType type,
                       Integer targetValue, Integer pointsReward) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.pointsReward = pointsReward;
        this.isActive = true;
        this.badgeIcon = generateBadgeIcon(type);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBadgeIcon() { return badgeIcon; }
    public void setBadgeIcon(String badgeIcon) { this.badgeIcon = badgeIcon; }

    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }

    public Integer getTargetValue() { return targetValue; }
    public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    private String generateBadgeIcon(AchievementType type) {
        switch (type) {
            case SAVINGS_STREAK: return "🔥";
            case NO_SPEND_DAY: return "💎";
            case BUDGET_CHAMPION: return "🏆";
            case CATEGORY_SAVER: return "💰";
            case WEEKLY_WARRIOR: return "⚔️";
            case EARLY_BIRD: return "🐦";
            case CONSISTENT_TRACKER: return "📊";
            case EXPENSE_EXPLORER: return "🧭";
            default: return "⭐";
        }
    }
}