package com.carousell.sample.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserLogoutRes(
    @SerializedName("ok") var ok: Boolean = false,
    @SerializedName("message") var message: String = "",
)