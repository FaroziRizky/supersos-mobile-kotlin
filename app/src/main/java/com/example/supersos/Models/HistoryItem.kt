package com.example.supersos.Models

import com.google.gson.annotations.SerializedName


data class HistoryItem (

    @SerializedName("status" ) var status : Int?              = null,
    @SerializedName("values" ) var values : ArrayList<Values> = arrayListOf()

)

data class Values (

    @SerializedName("id_call"           ) var idCall           : Int?    = null,
    @SerializedName("message"           ) var message          : String? = null,
    @SerializedName("latitude"          ) var latitude         : Double? = null,
    @SerializedName("longitude"         ) var longitude        : Double? = null,
    @SerializedName("applied_at"        ) var appliedAt        : String? = null,
    @SerializedName("answered_at"       ) var answeredAt       : String? = null,
    @SerializedName("status"            ) var status           : Int?    = null,
    @SerializedName("call_type"         ) var callType         : Int?    = null,
    @SerializedName("id_user"           ) var idUser           : Int?    = null,
    @SerializedName("fullname"          ) var fullname         : String? = null,
    @SerializedName("id_instances"      ) var idInstances      : String? = null,
    @SerializedName("instances_name"    ) var instancesName    : String? = null,
    @SerializedName("instances_address" ) var instancesAddress : String? = null,
    @SerializedName("instances_email"   ) var instancesEmail   : String? = null,
    @SerializedName("instances_phone"   ) var instancesPhone   : String? = null

)