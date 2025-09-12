package com.gijun.mainserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainServerApplication

fun main(args: Array<String>) {
    runApplication<MainServerApplication>(*args)
}
