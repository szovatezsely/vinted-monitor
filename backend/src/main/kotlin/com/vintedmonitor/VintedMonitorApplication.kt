package com.vintedmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VintedMonitorApplication

fun main(args: Array<String>) {
    runApplication<VintedMonitorApplication>(*args)
}
