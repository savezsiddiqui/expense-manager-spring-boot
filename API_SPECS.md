
# API Specifications - Expense Tracker

This document provides the OpenAPI 3.0 specification for the Expense Tracker API.

```yaml
openapi: 3.0.0
info:
  title: Expense Tracker API
  description: API for managing personal expenses.
  version: 1.0.0

servers:
  - url: /api

paths:
  /auth/register:
    post:
      summary: Register a new user
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                username:
                  type: string
                email:
                  type: string
                password:
                  type: string
      responses:
        '201':
          description: User registered successfully
        '400':
          description: Invalid input

  /auth/login:
    post:
      summary: Authenticate a user and get a JWT token
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              properties:
                username:
                  type: string
                password:
                  type: string
      responses:
        '200':
          description: Authentication successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  token:
                    type: string
        '401':
          description: Unauthorized

  /expenses:
    get:
      summary: Get all expenses for the current user
      security:
        - bearerAuth: []
      responses:
        '200':
          description: A list of expenses
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Expense'
        '401':
          description: Unauthorized
    post:
      summary: Create a new expense
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ExpenseInput'
      responses:
        '201':
          description: Expense created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Expense'
        '400':
          description: Invalid input
        '401':
          description: Unauthorized

  /expenses/{id}:
    get:
      summary: Get an expense by ID
      security:
        - bearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '200':
          description: The expense object
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Expense'
    put:
      summary: Update an expense by ID
      security:
        - bearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ExpenseInput'
      responses:
        '200':
          description: Expense updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Expense'
        '400':
          description: Invalid input
        '401':
          description: Unauthorized
        '404':
          description: Expense not found
    delete:
      summary: Delete an expense by ID
      security:
        - bearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: Expense deleted successfully
        '401':
          description: Unauthorized
        '404':
          description: Expense not found

  /categories:
    get:
      summary: Get all categories for the current user
      security:
        - bearerAuth: []
      responses:
        '200':
          description: A list of categories
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Category'
        '401':
          description: Unauthorized
    post:
      summary: Create a new category
      security:
        - bearerAuth: []
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CategoryInput'
      responses:
        '201':
          description: Category created successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Category'
        '400':
          description: Invalid input
        '401':
          description: Unauthorized

  /categories/{id}:
    put:
      summary: Update a category by ID
      security:
        - bearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CategoryInput'
      responses:
        '200':
          description: Category updated successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Category'
        '400':
          description: Invalid input
        '401':
          description: Unauthorized
        '404':
          description: Category not found
    delete:
      summary: Delete a category by ID
      security:
        - bearerAuth: []
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
      responses:
        '204':
          description: Category deleted successfully
        '401':
          description: Unauthorized
        '404':
          description: Category not found

components:
  schemas:
    Expense:
      type: object
      properties:
        id:
          type: integer
        amount:
          type: number
        date:
          type: string
          format: date
        description:
          type: string
        category:
          $ref: '#/components/schemas/Category'

    ExpenseInput:
      type: object
      properties:
        amount:
          type: number
        date:
          type: string
          format: date
        description:
          type: string
        categoryId:
          type: integer

    Category:
      type: object
      properties:
        id:
          type: integer
        name:
          type: string

    CategoryInput:
      type: object
      properties:
        name:
          type: string

  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
```
