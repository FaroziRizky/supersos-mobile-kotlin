package com.example.supersos.Models

import com.google.gson.annotations.SerializedName

data class UserNameResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("values") val values: List<User>
)

data class User(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("call_applied") val callApplied: Int
)
