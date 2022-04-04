package com.carousell.sample.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carousell.chat.models.CCMessage
import com.carousell.sample.databinding.AdapterInMessageItemBinding
import com.carousell.sample.databinding.AdapterOutMessageItemBinding
import com.carousell.sample.ui.interfaces.MessageActions
import com.carousell.sample.ui.viewholders.InMessageViewHolder
import com.carousell.sample.ui.viewholders.OutMessageViewHolder

class ChatRoomAdapter(private val messageActions: MessageActions, private val userId: String) :
    ListAdapter<CCMessage, RecyclerView.ViewHolder>(chatDiffCallback) {

    private val VIEW_TYPE_OUTGOING = 1
    private val VIEW_TYPE_INCOMING = 2

    companion object {
        private val chatDiffCallback = object : DiffUtil.ItemCallback<CCMessage>() {
            override fun areItemsTheSame(oldItem: CCMessage, newItem: CCMessage): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: CCMessage, newItem: CCMessage): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (userId.equals(getItem(position).senderId, true)) {
            VIEW_TYPE_OUTGOING
        } else {
            VIEW_TYPE_INCOMING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_OUTGOING -> OutMessageViewHolder(
                AdapterOutMessageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            VIEW_TYPE_INCOMING -> InMessageViewHolder(
                AdapterInMessageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> OutMessageViewHolder(
                AdapterOutMessageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_OUTGOING -> {
                val item = getItem(position)
                val onViewHolder = holder as OutMessageViewHolder
                onViewHolder.bind(position, item, messageActions)
            }
            VIEW_TYPE_INCOMING -> {
                val item = getItem(position)
                val onViewHolder = holder as InMessageViewHolder
                onViewHolder.bind(position, item)
            }
        }
    }
}