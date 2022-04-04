package com.carousell.sample.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.carousell.chat.models.CCChannel
import com.carousell.sample.databinding.AdapterChannelItemsBinding
import com.carousell.sample.ui.listeners.ChannelClickListener

class ChannelItemViewHolder(
    private val onChannelClickListener: ChannelClickListener,
    private val binding: AdapterChannelItemsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, ccChannel: CCChannel) {
        binding.ccChannel = ccChannel
        binding.root.setOnClickListener {
            onChannelClickListener.onChannelClick(
                it,
                ccChannel.channelId,
                ccChannel.lastMessageCreatedAt,
                ccChannel.avatar,
                ccChannel.name,
                ccChannel.itemName,
                ccChannel.itemPrice,
                ccChannel.itemImage
            )
        }

        onChannelClickListener.onChannelOnlineStatus(binding.txtOnline, ccChannel.sellerId)
    }
}