package com.carousell.sample.model

import com.carousell.chat.models.MetaData
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("access_token") var accessToken: String = "",
    @SerializedName("refresh_token") var refreshToken: String = "",
    @SerializedName("profile") var profile: Profile = Profile(),
    @SerializedName("metadata") var metaData: MetaData = MetaData(),
    @SerializedName("token") var token: String = ""
)
