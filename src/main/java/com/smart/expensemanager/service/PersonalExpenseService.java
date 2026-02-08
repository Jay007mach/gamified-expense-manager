package com.smart.expensemanager.service;

import com.smart.expensemanager.model.PersonalExpense;
import com.smart.expensemanager.repository.PersonalExpenseRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonalExpenseService {
    private final PersonalExpenseRepository repo;
    private final ApplicationEventPublisher eventPublisher;

    public PersonalExpenseService(PersonalExpenseRepository repo,
                                  ApplicationEventPublisher eventPublisher) {
        this.repo = repo;
        this.eventPublisher = eventPublisher;
    }

    public PersonalExpense save(PersonalExpense e) {
        PersonalExpense savedExpense = repo.save(e);
        // Publish event instead of direct service call
        eventPublisher.publishEvent(new ExpenseAddedEvent(this, savedExpense));
        return savedExpense;
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public PersonalExpense findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<PersonalExpense> listAll() {
        return repo.findAllByOrderByDateDesc();
    }

    public double totalForCurrentMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        Double total = repo.findTotalForMonth(startOfMonth, endOfMonth);
        return total == null ? 0.0 : total;
    }

    public double totalForCurrentWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        List<PersonalExpense> weekExpenses = repo.findByDateBetween(startOfWeek, endOfWeek);
        return weekExpenses.stream().mapToDouble(PersonalExpense::getAmount).sum();
    }

    public double totalForToday() {
        LocalDate today = LocalDate.now();
        List<PersonalExpense> todayExpenses = repo.findByDate(today);
        return todayExpenses.stream().mapToDouble(PersonalExpense::getAmount).sum();
    }

    public Map<String, Double> sumByCategoryCurrentMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        List<PersonalExpense> monthExpenses = repo.findByDateBetween(startOfMonth, endOfMonth);

        return monthExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() == null ? "Uncategorized" : e.getCategory(),
                        Collectors.summingDouble(PersonalExpense::getAmount)
                ));
    }

    public Map<String, Double> getLastSixMonthsTrend() {
        LocalDate now = LocalDate.now();
        Map<String, Double> monthlyTrends = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            LocalDate start = month.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate end = month.with(TemporalAdjusters.lastDayOfMonth());

            Double total = repo.findTotalForMonth(start, end);
            String monthName = month.getMonth().toString().substring(0, 3) + " " + month.getYear();
            monthlyTrends.put(monthName, total == null ? 0.0 : total);
        }

        return monthlyTrends;
    }

    // Event class for expense addition
    public static class ExpenseAddedEvent {
        private final Object source;
        private final PersonalExpense expense;

        public ExpenseAddedEvent(Object source, PersonalExpense expense) {
            this.source = source;
            this.expense = expense;
        }

        public Object getSource() { return source; }
        public PersonalExpense getExpense() { return expense; }
    }
}