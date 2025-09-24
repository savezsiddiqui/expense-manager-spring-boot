package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Expense> getExpenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        return expenseRepository.findByUserId(user.getId());
    }

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense expenseDetails) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setAmount(expenseDetails.getAmount());
        expense.setDate(expenseDetails.getDate());
        expense.setDescription(expenseDetails.getDescription());
        expense.setCategory(expenseDetails.getCategory());
        return ResponseEntity.ok(expenseRepository.save(expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteExpense(@PathVariable Long id) {
        expenseRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
