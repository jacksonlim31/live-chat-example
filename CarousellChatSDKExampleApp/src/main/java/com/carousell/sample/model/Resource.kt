package com.carousell.sample.model

import com.carousell.chat.remotes.common.ResultState

sealed class Resource<T>(
    val result: T? = null
) {
    class Loading(val action: Action) : Resource<ResultState>()
    class Loaded(val action: Action, val resultState: ResultState) : Resource<ResultState>(resultState)
    class Success(val action: Action, val resultState: ResultState): Resource<ResultState>(resultState)
    class Error(val action: Action, val resultState: ResultState): Resource<ResultState>(resultState)
    class UnexpectedError(val action: Action): Resource<ResultState>()
    class Logout(val action: Action): Resource<ResultState>()
}