package com.smart.expensemanager.repository;

import com.smart.expensemanager.model.PersonalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PersonalExpenseRepository extends JpaRepository<PersonalExpense, Long> {
    List<PersonalExpense> findAllByOrderByDateDesc();
    List<PersonalExpense> findByDateBetween(LocalDate start, LocalDate end);
    List<PersonalExpense> findByDate(LocalDate date);

    @Query("SELECT SUM(e.amount) FROM PersonalExpense e WHERE e.date >= :start AND e.date <= :end")
    Double findTotalForMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.category, SUM(e.amount) FROM PersonalExpense e WHERE e.date >= :start AND e.date <= :end GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> findTopCategories(@Param("start") LocalDate start, @Param("end") LocalDate end);
}