package com.example.expensetracker.payload;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponseDto {

    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private String categoryName;

    public ExpenseResponseDto(Long id, BigDecimal amount, LocalDate date, String description, String categoryName) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
