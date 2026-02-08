package com.smart.expensemanager.controller;

import com.smart.expensemanager.model.PersonalExpense;
import com.smart.expensemanager.service.PersonalExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/personal")
public class PersonalExpenseController {
    private final PersonalExpenseService service;
    private final List<String> expenseCategories = List.of("Food", "Transport", "Utilities", "Entertainment", "Healthcare", "Shopping", "Other");

    public PersonalExpenseController(PersonalExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("expenses", service.listAll());
        model.addAttribute("monthTotal", service.totalForCurrentMonth());
        model.addAttribute("categories", expenseCategories);
        return "personal/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("expense", new PersonalExpense());
        model.addAttribute("categories", expenseCategories);
        return "personal/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute PersonalExpense expense) {
        if (expense.getDate() == null) {
            expense.setDate(java.time.LocalDate.now());
        }
        service.save(expense);
        return "redirect:/personal";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        PersonalExpense expense = service.findById(id);
        if (expense != null) {
            model.addAttribute("expense", expense);
        } else {
            model.addAttribute("expense", new PersonalExpense());
        }
        model.addAttribute("categories", expenseCategories);
        return "personal/add";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/personal";
    }
}