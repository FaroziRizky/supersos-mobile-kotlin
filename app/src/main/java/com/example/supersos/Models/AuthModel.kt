package com.example.supersos.Models

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val message: String,
    val currUser: Int
)