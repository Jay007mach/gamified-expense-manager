package com.smart.expensemanager.repository;

import com.smart.expensemanager.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByIsActiveTrue();
    List<Achievement> findByType(Achievement.AchievementType type);
}