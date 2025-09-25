package com.example.expensetracker.service.impl;

import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.exception.ForbiddenException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.ExpenseRequest;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Expense> getExpensesForUser(User user) {
        return expenseRepository.findByUserId(user.getId());
    }

    @Override
    public Expense getExpenseById(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User is not authorized to access this expense");
        }
        return expense;
    }

    @Override
    public void createExpense(ExpenseRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + request.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Category does not belong to the current user.");
        }

        Expense newExpense = new Expense();
        newExpense.setAmount(request.getAmount());
        newExpense.setDate(request.getDate());
        newExpense.setDescription(request.getDescription());
        newExpense.setUser(user);
        newExpense.setCategory(category);

        expenseRepository.save(newExpense);
    }

    @Override
    public Expense updateExpense(Long id, ExpenseRequest request, User user) {
        Expense existingExpense = getExpenseById(id, user); // This already performs the ownership check

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + request.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Category does not belong to the current user.");
        }

        existingExpense.setAmount(request.getAmount());
        existingExpense.setDate(request.getDate());
        existingExpense.setDescription(request.getDescription());
        existingExpense.setCategory(category);

        return expenseRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(Long id, User user) {
        Expense expense = getExpenseById(id, user); // This already performs the ownership check
        expenseRepository.delete(expense);
    }
}
