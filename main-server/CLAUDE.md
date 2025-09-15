# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Multi-Store Management System using Kotlin Spring Boot with:

- Hexagonal Architecture (Ports and Adapters)
- CQRS Pattern
- Domain-Driven Design (DDD)

Domain hierarchy: HQ (Headquarters) → Store → POS

## Essential Development Commands

### Build and Run

```bash
# Build project
.\gradlew.bat build

# Run application
.\gradlew.bat bootRun

# Run tests
.\gradlew.bat test

# Clean build
.\gradlew.bat clean build

# Run specific test
.\gradlew.bat test --tests "TestClassName"
```

### Infrastructure Setup

```bash
# Start required services (MySQL, Redis, Kafka)
cd docker
docker-compose up -d

# Stop services
docker-compose down
```

Database: `jdbc:mysql://localhost:3306/main_server` (mainuser/mainpassword)

## Architecture

### Directory Structure

```
src/main/kotlin/com/gijun/mainserver/
├── application/          # Use cases, DTOs, handlers
│   ├── dto/             
│   │   ├── command/     # CQRS commands
│   │   └── query/       # CQRS queries
│   ├── handler/         # Command/Query handlers
│   └── port/            
│       ├── in/          # Use case interfaces
│       └── out/         # Repository interfaces
├── domain/              # Business entities and logic
│   ├── common/          # Value Objects (Money, Email, etc.)
│   ├── organization/    # Hq, Store, Pos
│   └── product/         # Product, ProductCost, ProductStock
└── infrastructure/      
    └── adapter/        
        ├── in/web/      # REST controllers
        └── out/persistence/  # JPA implementations
```

### Key Patterns

1. **Hexagonal Architecture**: Business logic isolated from infrastructure
    - Input ports define use cases
    - Output ports define repository interfaces
    - Adapters implement the ports

2. **CQRS**: Separate read/write operations
    - Commands: CreateXCommand, UpdateXCommand
    - Queries: GetXQuery, FindXQuery
    - Separate handlers for each

3. **Domain Model**: Rich domain entities with business logic
    - Entities extend BaseEntity (id, createdAt, updatedAt)
    - Value Objects for type safety (Money, Quantity, etc.)

### API Pattern

All endpoints follow: `/main/[domain]/[resource]`

- POST /main/organization/hq (Create)
- GET /main/organization/hq/{id} (Read)
- PUT /main/organization/hq/{id} (Update)
- DELETE /main/organization/hq/{id} (Delete)

## Development Considerations

1. **BaseEntity**: All entities inherit from BaseEntity with audit fields
2. **Mappers**: Use dedicated mapper classes for DTO↔Domain conversion
3. **Repositories**: Separate Command and Query repositories per CQRS
4. **Events**: Domain events for inter-aggregate communication
5. **Testing**: Test files mirror source structure, use Spring Boot Test

## Coding Principles (MANDATORY)

### 1. Single Class Per File Rule
- **RULE**: One file must contain exactly one class, interface, enum, or object
- **VIOLATION**: Having multiple classes in a single file is strictly prohibited
- **EXAMPLES**:
  - ✅ `AdjustProductStockCommand.kt` contains only `AdjustProductStockCommand` class
  - ✅ `StockAdjustmentType.kt` contains only `StockAdjustmentType` enum
  - ❌ One file containing both `ApiResponse` and `ErrorDetail` classes

### 2. Custom Domain Exceptions Only Rule
- **RULE**: Use ONLY custom domain exceptions - NO basic Java/Kotlin exceptions allowed
- **VIOLATION**: Using `IllegalArgumentException`, `NoSuchElementException`, etc. is strictly prohibited
- **MANDATORY APPROACH**:
  1. Define ALL exceptions in `domain.common.exception` package
  2. All custom exceptions must extend `DomainException` base class
  3. Business logic throws custom domain exceptions only
  4. `GlobalExceptionHandler` catches custom exceptions and returns `ApiResponse.error()`
- **AVAILABLE CUSTOM EXCEPTIONS**:
  - `InvalidArgumentException` - for invalid input parameters
  - `EntityNotFoundException` - for missing entities
  - `DuplicateEntityException` - for duplicate entity creation
  - `ValidationException` - for domain validation failures
  - `NullIdException` - for null ID errors
- **EXAMPLES**:
  - ✅ `throw InvalidArgumentException("Invalid product ID")` (business logic)
  - ✅ `throw EntityNotFoundException("Product", productId)` (not found)
  - ✅ `throw ValidationException("Price cannot be negative")` (domain validation)
  - ❌ `throw IllegalArgumentException("...")` (basic exception)
  - ❌ `throw NoSuchElementException("...")` (basic exception)
- **IMPLEMENTATION**:
  - `GlobalExceptionHandler` handles: `InvalidArgumentException`, `EntityNotFoundException`, `DuplicateEntityException`, etc.
  - All handlers return `ResponseEntity<ApiResponse<Nothing>>` with appropriate HTTP status
  - Error codes: `INVALID_ARGUMENT`, `ENTITY_NOT_FOUND`, `DUPLICATE_ENTITY`, `VALIDATION_ERROR`, `INTERNAL_ERROR`

### 3. No Value Objects (VOs) Rule
- **RULE**: Value Objects (VOs) are strictly prohibited - use primitive types only
- **VIOLATION**: Creating or using any Value Object classes is forbidden
- **APPROACH**:
  - Use `String` instead of `Email`, `PhoneNumber`, `Address` VOs
  - Use `BigDecimal` instead of `Money`, `Quantity` VOs
  - Use `Long` instead of ID VOs (HqId, StoreId, ProductId, etc.)
  - Use `String` instead of `ProductCode` VOs
  - Validate using domain model `init` blocks with `require()` statements
- **EXAMPLES**:
  - ✅ `val email: String` with validation in domain model
  - ✅ `val price: BigDecimal` with `require(price >= BigDecimal.ZERO)`
  - ✅ `val hqId: Long` instead of `HqId` VO
  - ❌ `data class Email(val value: String)`
  - ❌ `data class Money(val value: BigDecimal)`
  - ❌ `data class ProductId(val value: Long)`

## Current Branch

Working on: `main-server-application-layer` - implementing application layer with CQRS handlers and use cases.