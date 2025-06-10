package com.innosage.cmp.example.roomdemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform