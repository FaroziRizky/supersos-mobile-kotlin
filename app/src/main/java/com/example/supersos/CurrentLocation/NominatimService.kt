package com.example.supersos.CurrentLocation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {
    @GET("reverse")
    fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json"
    ): Call<NominatimResponse>
}