package com.carousell.sample

import androidx.multidex.MultiDexApplication
import com.carousell.chat.configs.CCChatSDK
import com.carousell.chat.configs.ChatVersion
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        CCChatSDK(this)
            .setChatBaseUrl("https://ct-chat-api-dev.cmco.io/")
            .setChatSocketBaseUrl("https://ct-chat-socket-dev.cmco.io")
            .setChatVersion(ChatVersion.V1)
            .build()
    }
}