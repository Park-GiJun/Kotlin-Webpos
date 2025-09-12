package com.gijun.mainserver.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

@Configuration
@Profile("cqrs")
class DataSourceConfig {

    @Bean
    fun writeDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl =
                "jdbc:mysql://localhost:3306/main_server?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul"
            username = "mainuser"
            password = "mainpassword"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            poolName = "WriteHikariPool"
            minimumIdle = 5
            maximumPoolSize = 15
            connectionTimeout = 20000
        }
        return HikariDataSource(config)
    }

    @Bean
    fun readDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl =
                "jdbc:mysql://localhost:3307/main_server?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul"
            username = "readonly_user"
            password = "readonly_password"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            poolName = "ReadHikariPool"
            minimumIdle = 10
            maximumPoolSize = 30
            connectionTimeout = 10000
            isReadOnly = true
        }
        return HikariDataSource(config)
    }

    @Bean
    fun routingDataSource(
        @Qualifier("writeDataSource") writeDataSource: DataSource,
        @Qualifier("readDataSource") readDataSource: DataSource
    ): DataSource {
        val routingDataSource = TransactionRoutingDataSource()

        val dataSourceMap = mapOf<Any, Any>(
            DataSourceType.WRITE to writeDataSource,
            DataSourceType.READ to readDataSource
        )

        routingDataSource.setTargetDataSources(dataSourceMap)
        routingDataSource.setDefaultTargetDataSource(writeDataSource)
        routingDataSource.afterPropertiesSet()

        return routingDataSource
    }

    @Bean
    @Primary
    fun dataSource(@Qualifier("routingDataSource") routingDataSource: DataSource): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}

enum class DataSourceType {
    WRITE, READ
}

class TransactionRoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        return if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            DataSourceType.READ
        } else {
            DataSourceType.WRITE
        }
    }
}

object DataSourceContextHolder {
    private val contextHolder = ThreadLocal<DataSourceType>()

    fun setDataSourceType(dataSourceType: DataSourceType) {
        contextHolder.set(dataSourceType)
    }

    fun getDataSourceType(): DataSourceType {
        return contextHolder.get() ?: DataSourceType.WRITE
    }

    fun clear() {
        contextHolder.remove()
    }
}