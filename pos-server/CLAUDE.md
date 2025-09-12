# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with pos-server code in this repository.

## Project Overview

POS (Point of Sale) Server using Kotlin Spring Boot with:

- Hexagonal Architecture (Ports and Adapters)
- CQRS Pattern
- Domain-Driven Design (DDD)

This server handles POS operations and communicates with the main-server.

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

Database: `jdbc:mysql://localhost:3306/pos_server` (posuser/pospassword)

## Architecture

### Directory Structure

```
src/main/kotlin/com/gijun/posserver/
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
│   ├── sales/           # Sales, Receipt, Payment
│   └── inventory/       # Stock, Product sync
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

All endpoints follow: `/pos/[domain]/[resource]`

- POST /pos/sales/transaction (Create)
- GET /pos/sales/transaction/{id} (Read)
- PUT /pos/sales/transaction/{id} (Update)
- DELETE /pos/sales/transaction/{id} (Delete)

## Development Considerations

1. **BaseEntity**: All entities inherit from BaseEntity with audit fields
2. **Mappers**: Use dedicated mapper classes for DTO↔Domain conversion
3. **Repositories**: Separate Command and Query repositories per CQRS
4. **Events**: Domain events for inter-aggregate communication
5. **Testing**: Test files mirror source structure, use Spring Boot Test
6. **Integration**: Communicate with main-server via Kafka/HTTP

## Server Configuration

- Port: 8081
- Context Path: /pos
- Database: pos_server
- User: posuser/pospassword