package com.smart.expensemanager.repository;

import com.smart.expensemanager.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByIsUnlockedTrue();
    List<UserAchievement> findByIsUnlockedFalse();
    Optional<UserAchievement> findByAchievementId(Long achievementId);
    Integer countByIsUnlockedTrue();
}