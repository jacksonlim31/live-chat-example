package com.carousell.sample.remotes

import com.carousell.sample.remotes.services.ChatDemoApiService
import okhttp3.Request

object ChatDemoClient: ChatBaseRemote() {

    internal fun getInstances(baseURL: String): ChatDemoApiService {
        return create(baseURL, ChatDemoApiService::class.java)
    }

    internal fun getRequest(original: Request, headersMap: Map<String, String>): Request.Builder {
        val requestBuilder = original.newBuilder()
        for ((k, v) in headersMap) {
            requestBuilder.addHeader(k, v)
        }
        requestBuilder.method(original.method(), original.body())
        return requestBuilder
    }
}