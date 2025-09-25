package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.ExpenseResponseDto;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<?> getExpenses() {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isPresent()) {
            List<Expense> expenses = expenseRepository.findByUserId(optionalUser.get().getId());
            List<ExpenseResponseDto> expenseDtos = expenses.stream()
                    .map(this::convertToExpenseDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(expenseDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody Expense expense) {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            return new ResponseEntity<>("Category is missing or invalid.", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> optionalCategory = categoryRepository.findById(expense.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>("Category not found.", HttpStatus.BAD_REQUEST);
        }
        Category category = optionalCategory.get();
        if (!category.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>("Category does not belong to the current user.", HttpStatus.BAD_REQUEST);
        }

        expense.setUser(user);
        expense.setCategory(category);
        expenseRepository.save(expense);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id) {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            if (!expense.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(expense, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody Expense expenseDetails) {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        if (expenseDetails.getCategory() == null || expenseDetails.getCategory().getId() == null) {
            return new ResponseEntity<>("Category is missing or invalid.", HttpStatus.BAD_REQUEST);
        }
        Optional<Category> optionalCategory = categoryRepository.findById(expenseDetails.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>("Category not found.", HttpStatus.BAD_REQUEST);
        }
        Category category = optionalCategory.get();
        if (!category.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>("Category does not belong to the current user.", HttpStatus.BAD_REQUEST);
        }

        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            if (!expense.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            expense.setAmount(expenseDetails.getAmount());
            expense.setDate(expenseDetails.getDate());
            expense.setDescription(expenseDetails.getDescription());
            expense.setCategory(category);
            Expense updatedExpense = expenseRepository.save(expense);
            return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            if (!expense.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            expenseRepository.delete(expense);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ExpenseResponseDto convertToExpenseDto(Expense expense) {
        return new ExpenseResponseDto(
                expense.getId(),
                expense.getAmount(),
                expense.getDate(),
                expense.getDescription(),
                expense.getCategory().getName()
        );
    }
}
