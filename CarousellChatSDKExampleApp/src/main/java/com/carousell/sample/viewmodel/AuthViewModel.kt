package com.carousell.sample.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carousell.chat.managers.ChatManager
import com.carousell.chat.managers.TokenStatus
import com.carousell.chat.remotes.common.ResultState
import com.carousell.sample.configs.Constant
import com.carousell.sample.model.Action
import com.carousell.sample.model.Resource
import com.carousell.sample.model.UserLoginParams
import com.carousell.sample.repository.ChatDemoRepository
import com.carousell.sample.utils.ChatUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val chatDemoRepository: ChatDemoRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val authLiveData: MutableLiveData<Resource<ResultState>> = MutableLiveData()
    private val authManager = ChatManager.createAuthManager()

    fun login(context: Context, phone: String, password: String) {
        authLiveData.postValue(Resource.Loading(Action.CHAT_LOGIN))
        val userLoginParams = UserLoginParams(phone, password)
        compositeDisposable.add(chatDemoRepository.login("v1", userLoginParams)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                ChatUtil.saveClassInSharedPreferences(context, Constant.USER_DATA, it)
                exchangeToken(it.profile.accountId.toString(), it.accessToken)
            }, {
                authLiveData.postValue(Resource.UnexpectedError(Action.CHAT_LOGIN))
            })
        )
    }

    fun logout(accessToken: String) {
        authLiveData.postValue(Resource.Loading(Action.CHAT_LOGOUT))
        compositeDisposable.add(chatDemoRepository.logout("v1", accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
               authLiveData.postValue(Resource.Logout(Action.CHAT_LOGOUT))
            }, {
                authLiveData.postValue(Resource.UnexpectedError(Action.CHAT_LOGIN))
            })
        )
    }

    private fun exchangeToken(userId: String, accessToken: String) {
        val authorization = "Bearer $accessToken"
        compositeDisposable.add(
            chatDemoRepository.exchangeTokens("v2", authorization)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.code == 200) {
                      loginChat(userId, it.data.accessToken, it.data.refreshToken)
                    }
                }, {
                    authLiveData.postValue(Resource.UnexpectedError(Action.CHAT_LOGIN))
                }))
    }

    private fun loginChat(userId: String, accessToken: String, refreshToken: String) {
        authLiveData.postValue(Resource.Loading(Action.CHAT_LOGIN))
        authManager?.login(userId, accessToken, refreshToken) {
            authLiveData.postValue(Resource.Loaded(Action.CHAT_LOGIN, it))
            authManager.dispose()
        }
    }

    fun isAuthenticated(userId: String, onCompleteCallback: (isAuthenticate: Boolean) -> Unit) {
        authManager?.isAuthenticated(userId) { resultState ->
            when {
                resultState is ResultState.ChatTokenStatus -> {
                    if (resultState.tokenStatus.name.equals(TokenStatus.ACTIVE.name, true)) {
                        onCompleteCallback(true)
                    } else {
                        onCompleteCallback(false)
                    }
                }
            }
        }
    }

    fun clearChatSDK() {
        authManager?.logout { resultState ->
            when(resultState) {
                is ResultState.Success<*> -> {
                    authLiveData.postValue(Resource.Success(Action.CHAT_LOGOUT, resultState))
                }
                is ResultState.Error -> {
                    authLiveData.postValue(Resource.Error(Action.CHAT_LOGOUT, resultState))
                }
                else -> {
                    authLiveData.postValue(Resource.UnexpectedError(Action.CHAT_LOGOUT))
                }
            }
        }
    }

    fun dispose() {
        authManager?.dispose()
        compositeDisposable.dispose()
    }
}