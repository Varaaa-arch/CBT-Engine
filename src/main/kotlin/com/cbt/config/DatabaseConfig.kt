package com.cbt.config

import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun init(config: ApplicationConfig): Database {
        val db = Database.connect(
            url = config.property("database.url").getString(),
            driver = "org.postgresql.Driver",
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString()
        )
        println("DATABASE CONNECTED ✅")
        return db
    }
}