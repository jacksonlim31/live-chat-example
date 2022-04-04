package com.carousell.sample.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Profile(
    @SerializedName("account_id") var accountId: Long = 0,
    @SerializedName("account_oid") var accountOId: String = "",
    @SerializedName("address") var address: String = "",
    @SerializedName("create_time") var createTime: Long = 0,
    @SerializedName("email") var email: String = "",
    @SerializedName("email_verified") var emailVerified: String = "",
    @SerializedName("facebook_token") var facebookToken: String = "",
    @SerializedName("favourites") var favourites: List<String> = listOf(),
    @SerializedName("full_name") var fullname: String = "",
    @SerializedName("is_active") var isActive: Boolean = false,
    @SerializedName("is_payoo_linked") var isPayooLinked: Boolean = false,
    @SerializedName("long_term_facebook_token") var longTermFacebookToken: String = "",
    @SerializedName("phone") var phone: String = "",
    @SerializedName("phone_verified") var phoneVerified: String = "",
    @SerializedName("start_time") var startTime: Long = 0,
    @SerializedName("update_time") var updateTime: Long = 0,
)
