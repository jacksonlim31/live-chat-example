package com.carousell.sample.ui.interfaces

import android.view.View
import com.carousell.chat.models.CCMessage

interface MessageActions {
    fun onMessageClicked(view: View, position: Int, ccMessage: CCMessage)
    fun onMessageLongClicked(view: View, position: Int, ccMessage: CCMessage)
}