package com.carousell.sample.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carousell.chat.models.CCChannel
import com.carousell.sample.R
import com.carousell.sample.databinding.AdapterChannelItemsBinding
import com.carousell.sample.ui.listeners.ChannelClickListener
import com.carousell.sample.ui.viewholders.ChannelItemViewHolder

class ChannelAdapter(
    private val onChannelClickListener: ChannelClickListener
): ListAdapter<CCChannel, RecyclerView.ViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<CCChannel>() {
            override fun areItemsTheSame(oldItem: CCChannel, newItem: CCChannel): Boolean {
                return oldItem.channelId == newItem.channelId
            }

            override fun areContentsTheSame(oldItem: CCChannel, newItem: CCChannel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
       return R.layout.adapter_channel_items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.adapter_channel_items -> {
                val binding = AdapterChannelItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChannelItemViewHolder(onChannelClickListener, binding)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.adapter_channel_items -> {
                val item = getItem(position)
                val onViewHolder = holder as ChannelItemViewHolder
                onViewHolder.bind(position, item)
            }
        }
    }
}