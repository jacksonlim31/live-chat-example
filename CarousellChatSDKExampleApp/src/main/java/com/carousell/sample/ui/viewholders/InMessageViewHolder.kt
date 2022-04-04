package com.carousell.sample.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.carousell.chat.models.CCMessage
import com.carousell.sample.databinding.AdapterInMessageItemBinding

class InMessageViewHolder(
    private val binding: AdapterInMessageItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(position: Int, ccMessage: CCMessage) {
        binding.ccMessage = ccMessage
        if(ccMessage.imageUrls.isNullOrEmpty()) {
            binding.imvAds.visibility = View.GONE
        } else {
            binding.imvAds.visibility = View.VISIBLE
        }
    }
}