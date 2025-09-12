package com.gijun.mainserver.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class TransactionConfig {
    // Spring Boot will auto-configure transaction manager with single datasource
    // No custom configuration needed for single database setup
}