package com.carousell.sample.ui.adapters

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.carousell.sample.utils.ChatUtil

@BindingAdapter("imageUrl")
fun loadImageUrl(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        if (it.contains(".svg")){
            imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        }
        ChatUtil.loadUrlImage(imageView.context, imageView, it)
    }
}