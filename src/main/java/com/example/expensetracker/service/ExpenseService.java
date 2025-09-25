package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.ExpenseRequest;

import java.util.List;

public interface ExpenseService {
    List<Expense> getExpensesForUser(User user);
    Expense getExpenseById(Long id, User user);
    void createExpense(ExpenseRequest request, User user);
    Expense updateExpense(Long id, ExpenseRequest request, User user);
    void deleteExpense(Long id, User user);
}
