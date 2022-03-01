package com.example.passengerapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Airline(
    @Expose @SerializedName("country") val country: String?,
    @Expose @SerializedName("established") val established: String?,
    @Expose @SerializedName("head_quaters") val headQuaters: String?,
    @Expose @SerializedName("id") val id: Double?,
    @Expose @SerializedName("logo") val logo: String?,
    @Expose @SerializedName("name") val name: String?,
    @Expose @SerializedName("slogan") val slogan: String?,
    @Expose @SerializedName("website") val website: String?
)