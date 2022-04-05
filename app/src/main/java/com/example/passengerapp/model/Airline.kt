package com.example.passengerapp.model

import com.google.gson.annotations.SerializedName

data class Airline(
    @SerializedName("country") val country: String?,
    @SerializedName("established") val established: String?,
    @SerializedName("head_quaters") val headQuaters: String?,
    @SerializedName("id") val id: Double?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("slogan") val slogan: String?,
    @SerializedName("website") val website: String?
) {
    fun isValid(): Boolean =
        !(logo == null || logo.isEmpty() ||
                country == null || country.isEmpty() ||
                established == null || established.isEmpty() ||
                headQuaters == null || headQuaters.isEmpty() ||
                id == null || id == 0.0 ||
                slogan == null || slogan.isEmpty() ||
                website == null || website.isEmpty())

}