package com.example.expensetracker.service;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<Category> getCategoriesForUser(User user);
    void createCategory(CategoryRequest request, User user);
    Category updateCategory(Long id, CategoryRequest request, User user);
    void deleteCategory(Long id, User user);
}
