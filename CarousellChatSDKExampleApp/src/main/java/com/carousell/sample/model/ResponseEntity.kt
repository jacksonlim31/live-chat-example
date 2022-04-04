package com.carousell.sample.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResponseEntity<T>(
    @SerializedName("code") var code: Int = 0,
    @SerializedName("data") var data: T,
)
