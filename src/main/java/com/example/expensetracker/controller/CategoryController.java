package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.User;
import com.example.expensetracker.payload.CategoryResponseDto;
import com.example.expensetracker.repository.CategoryRepository;
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
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryController(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String email = authentication.getName(); // This is now an email
        return userRepository.findByEmail(email); // Find by email instead
    }

    @GetMapping
    public ResponseEntity<?> getCategories() {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Category> categories = categoryRepository.findByUserId(user.getId());
            List<CategoryResponseDto> categoryDtos = categories.stream()
                    .map(this::convertToCategoryDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        Optional<User> optionalUser = getAuthenticatedUser();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (categoryRepository.findByNameAndUser(category.getName(), user).isPresent()) {
                return new ResponseEntity<>("A category with this name already exists.", HttpStatus.BAD_REQUEST);
            }
            category.setUser(user);
            categoryRepository.save(category);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(categoryDetails.getName());
            Category updatedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(updatedCategory);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private CategoryResponseDto convertToCategoryDto(Category category) {
        return new CategoryResponseDto(category.getId(), category.getName());
    }
}
