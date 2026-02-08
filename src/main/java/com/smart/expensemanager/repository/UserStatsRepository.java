package com.smart.expensemanager.repository;

import com.smart.expensemanager.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    // We'll have only one UserStats for the single user
}