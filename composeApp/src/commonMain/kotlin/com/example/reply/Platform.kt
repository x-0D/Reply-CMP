package com.example.reply

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform