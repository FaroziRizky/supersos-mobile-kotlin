package com.example.supersos.Models

import com.google.gson.annotations.SerializedName

data class MakeCallResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("values") val values: ResponseValues
)

data class ResponseValues(
    @SerializedName("fieldCount") val fieldCount: Int,
    @SerializedName("affectedRows") val affectedRows: Int,
    @SerializedName("insertId") val insertId: Int,
    @SerializedName("serverStatus") val serverStatus: Int,
    @SerializedName("warningCount") val warningCount: Int,
    @SerializedName("message") val message: String,
    @SerializedName("protocol41") val protocol41: Boolean,
    @SerializedName("changedRows") val changedRows: Int
)

data class MakeCallRequest(
    @SerializedName("message") val message: String,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("type") val type: Int
)
data class CancelCallRequest(
    @SerializedName("id_call") val idCall: String,
)
