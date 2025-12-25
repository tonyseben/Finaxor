package com.tonyseben.finaxor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform