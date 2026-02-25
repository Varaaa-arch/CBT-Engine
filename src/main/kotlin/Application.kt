package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    println("MAIN JALAN")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    println("MODULE KELOAD 🔥")

    routing {
        println("ROUTING KELOAD 🔥")

        get("/") {
            call.respondText("Backend")
        }

        get("/ping") {
            call.respondText("xirpl")
        }
    }
}
