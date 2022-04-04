package com.carousell.sample.ui.listeners

import android.view.View

interface ChannelClickListener {
    fun onChannelClick(
        view: View,
        channelId: String,
        lastMessageCreatedAt: Long,
        userAvatar: String,
        username: String,
        itemName: String,
        itemPrice: String,
        itemImage: String
    )

    fun onChannelOnlineStatus(
        view: View,
        sellerId: String
    )
}