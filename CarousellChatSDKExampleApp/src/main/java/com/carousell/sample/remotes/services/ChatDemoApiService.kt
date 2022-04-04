package com.carousell.sample.remotes.services

import com.carousell.sample.model.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface ChatDemoApiService {

    @POST("{version}/public/auth/login")
    fun login(
        @Path("version") version: String,
        @Body userLoginParams: UserLoginParams
    ): Observable<User>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("{version}/private/auth/logout")
    fun logout(
        @Path("version") version: String,
        @Header("Authorization") authorization: String
    ): Observable<UserLogoutRes>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @POST("{version}/public/chat/grp/getToken")
    fun exchangeChatToken(
        @Path("version") version: String,
        @Header("Authorization") authorization: String
    ): Observable<ResponseEntity<Tokens>>
}