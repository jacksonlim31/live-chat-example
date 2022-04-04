package com.carousell.sample.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserLoginParams(
    @SerializedName("phone") var phone: String = "",
    @SerializedName("password") var password: String = "",
)
