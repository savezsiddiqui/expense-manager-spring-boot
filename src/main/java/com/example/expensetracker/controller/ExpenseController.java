package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.ExpenseRequest;
import com.example.expensetracker.payload.ExpenseResponseDto;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expense Management", description = "APIs for managing expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database. This should not happen."));
    }

    @Operation(summary = "Get all expenses for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved expenses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getExpenses() {
        User user = getAuthenticatedUser();
        List<Expense> expenses = expenseService.getExpensesForUser(user);
        List<ExpenseResponseDto> expenseDtos = expenses.stream()
                .map(this::convertToExpenseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDtos);
    }

    @Operation(summary = "Create a new expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Expense created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseRequest request) {
        User user = getAuthenticatedUser();
        expenseService.createExpense(request, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get a single expense by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved expense"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User not authorized to access this expense"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        Expense expense = expenseService.getExpenseById(id, user);
        return ResponseEntity.ok(expense);
    }

    @Operation(summary = "Update an existing expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this expense"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        User user = getAuthenticatedUser();
        Expense updatedExpense = expenseService.updateExpense(id, request, user);
        return ResponseEntity.ok(updatedExpense);
    }

    @Operation(summary = "Delete an expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expense deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this expense"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
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
