package com.carousell.sample.ui.viewholders

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import androidx.recyclerview.widget.RecyclerView
import com.carousell.chat.models.CCMessage
import com.carousell.sample.databinding.AdapterOutMessageItemBinding
import com.carousell.sample.ui.interfaces.MessageActions

class OutMessageViewHolder(
    private val binding: AdapterOutMessageItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, ccMessage: CCMessage, messageActions: MessageActions) {
        binding.ccMessage = ccMessage
        if(ccMessage.imageUrls.isNullOrEmpty()) {
            binding.imvAds.visibility = View.GONE
        } else {
            binding.imvAds.visibility = View.VISIBLE
        }

        binding.llMessageBox.setOnLongClickListener {
            messageActions.onMessageLongClicked(it, position, ccMessage)
            true
        }

        binding.llMessageBox.setOnClickListener {
            messageActions.onMessageClicked(it, position, ccMessage)
        }
    }
}