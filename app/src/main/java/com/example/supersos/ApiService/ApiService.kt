package com.example.supersos.ApiService

import com.example.supersos.CallHistoryDetail
import com.example.supersos.Models.CancelCallRequest
import com.example.supersos.Models.HistoryItem
import com.example.supersos.Models.LoginRequest
import com.example.supersos.Models.LoginResponse
import com.example.supersos.Models.MakeCallRequest
import com.example.supersos.Models.MakeCallResponse
import com.example.supersos.Models.UserNameResponse
import com.example.supersos.Models.UserProfile
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("api/user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>


    @GET("api/user/name")
    fun getUserName(@Header("Authorization") token: String): Call<UserNameResponse>

    @GET("/api/user/call/histories")
    fun getHistoryALl(@Header("Authorization") token: String): Call<HistoryItem>

    @GET("/api/user/call/history/{id_call}")
    fun getCallDetail(
        @Header("Authorization") token: String,
        @Path("id_call") idCall: Int
    ): Call<HistoryItem>

    @POST("/api/user/call/make")
    fun makeCall(
        @Header("Authorization") token: String,
        @Body request: MakeCallRequest
    ): Call<MakeCallResponse>

    @PUT("/api/user/call/cancel")
    fun cancelCall(
        @Header("Authorization") token: String,
        @Body request: CancelCallRequest
    ): Call<Void?>?

    @GET("/api/user/profile")
    fun getUserProfile(@Header("Authorization") authToken: String): Call<UserProfile>

    @PUT("/api/user/profile/edit")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @POST("/api/user/register")
    fun register(
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @PUT("/api/user/profile/edit/password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @Multipart
    @PUT("/api/user/profile/edit/picture")
    fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Part picture: MultipartBody.Part
    ): Call<Void>


}