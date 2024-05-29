package com.example.supersos.Models

import com.google.gson.annotations.SerializedName

data class UserProfile (

    @SerializedName("status" ) var status : Int?              = null,
    @SerializedName("values" ) var values : ArrayList<userProfileResponse> = arrayListOf()

)

data class userProfileResponse(
    @SerializedName("id_user") val userId: Int,
    @SerializedName("fullname") val fullName: String,
    @SerializedName("address") val address: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("status") val status: Int,
    @SerializedName("picture") val pictureUrl: String,
    @SerializedName("call_applied") val callApplied: Int
)
