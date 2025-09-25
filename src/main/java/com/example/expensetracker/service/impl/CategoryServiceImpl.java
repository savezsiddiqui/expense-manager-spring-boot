package com.example.expensetracker.service.impl;

import com.example.expensetracker.exception.BadRequestException;
import com.example.expensetracker.exception.ForbiddenException;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.CategoryRequest;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findByUserId(user.getId());
    }

    @Override
    public void createCategory(CategoryRequest request, User user) {
        if (categoryRepository.findByNameAndUser(request.getName(), user).isPresent()) {
            throw new BadRequestException("A category with this name already exists.");
        }
        Category newCategory = new Category();
        newCategory.setName(request.getName());
        newCategory.setUser(user);
        categoryRepository.save(newCategory);
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request, User user) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User is not authorized to update this category");
        }

        if (categoryRepository.findByNameAndUser(request.getName(), user).isPresent() && !existingCategory.getName().equals(request.getName())) {
            throw new BadRequestException("A category with this name already exists.");
        }

        existingCategory.setName(request.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id, User user) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("User is not authorized to delete this category");
        }
        categoryRepository.delete(category);
    }
}
