package com.example.supersos.CurrentLocation

data class NominatimResponse(
    val address: Address
)

data class Address(
    val village: String?,
    val town: String?,
    val city: String?,
    val county: String?,
    val state: String?,
    val country: String?
)

