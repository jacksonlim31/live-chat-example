package com.carousell.sample.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Tokens(
    @SerializedName("access_token") val accessToken: String = "",
    @SerializedName("refresh_token") val refreshToken: String = ""
)
