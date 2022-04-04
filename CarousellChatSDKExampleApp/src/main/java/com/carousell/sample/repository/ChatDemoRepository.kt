package com.carousell.sample.repository

import com.carousell.sample.model.*
import com.carousell.sample.remotes.services.ChatDemoApiService
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ChatDemoRepository @Inject constructor(
    private val chatDemoApiService: ChatDemoApiService
) {
    fun login(version: String, userLoginParams: UserLoginParams): Observable<User> {
        return chatDemoApiService.login(version, userLoginParams)
    }

    fun exchangeTokens(version: String, authorization: String): Observable<ResponseEntity<Tokens>> {
        return chatDemoApiService.exchangeChatToken(version, authorization)
    }

    fun logout(version: String, accessToken: String): Observable<UserLogoutRes> {
        val authorization = "Bearer $accessToken"
        return chatDemoApiService.logout(version, authorization)
    }
}