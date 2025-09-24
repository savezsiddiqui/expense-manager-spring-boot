package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryController(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Category> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        return categoryRepository.findByUserId(user.getId());
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(categoryDetails.getName());
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
