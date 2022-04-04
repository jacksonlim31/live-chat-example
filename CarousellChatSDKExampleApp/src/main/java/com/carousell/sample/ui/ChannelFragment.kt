package com.carousell.sample.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.carousell.chat.models.CCChannel
import com.carousell.chat.models.CCChannelList
import com.carousell.chat.models.CCMessageStatus
import com.carousell.chat.remotes.common.ResultState
import com.carousell.sample.R
import com.carousell.sample.databinding.FragmentAuthBinding
import com.carousell.sample.databinding.FragmentChannelBinding
import com.carousell.sample.model.Action
import com.carousell.sample.model.Resource
import com.carousell.sample.ui.adapters.ChannelAdapter
import com.carousell.sample.ui.listeners.ChannelClickListener
import com.carousell.sample.viewmodel.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChannelFragment : Fragment(R.layout.fragment_channel), ChannelClickListener {
    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChannelFragmentArgs by navArgs()
    private var channelBinding: FragmentChannelBinding? = null
    private val binding get() = channelBinding!!
    private lateinit var channelList: List<CCChannel>

    override fun onStart() {
        super.onStart()
        if (!chatViewModel.isConnected()) {
            chatViewModel.reconnectSocket {
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        channelBinding = FragmentChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelBinding?.let { chnBinding ->
            val channelAdapter = ChannelAdapter(this)
            chnBinding.rvListView.adapter = channelAdapter
            chatViewModel.getBlockAndBanInfo()
            chatViewModel.getChannelList(0, 50, null)
            chatViewModel.channelLiveData.observe(viewLifecycleOwner, { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        chnBinding.progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        chnBinding.progress.visibility = View.GONE
                        resource.result?.let { resultState ->
                            when (resultState) {
                                is ResultState.Success<*> -> {
                                    when (resource.action) {
                                        Action.RETRIEVE_CHANNEL_LIST -> {
                                            if (resultState.result is CCChannelList) {
                                                channelList =
                                                    (resultState.result as CCChannelList).channelList
                                                channelAdapter.submitList(channelList)
                                                chnBinding.swRefreshView.isRefreshing = false
                                            }
                                        }
                                        Action.CREATE_CHANNEL -> {
                                            chatViewModel.getChannelList(0, 50, null)
                                        }
                                        Action.READ_STATUS -> {
                                            chatViewModel.getChannelList(0, 50, null)
                                        }
                                    }
                                }
                                else -> {
                                    showChannelError()
                                }
                            }
                        }
                    }
                    else -> {
                        chnBinding.progress.visibility = View.GONE
                        showChannelError()
                    }
                }
            })

            chnBinding.swRefreshView.setOnRefreshListener {
                chatViewModel.getChannelList(0, 50, null)
            }

            chnBinding.floatingActionButton.setOnClickListener {
                showRequiredListIdDialog(it)
            }

            chatViewModel.onReadStatus()
            chatViewModel.getTotalUnreadCount { resource ->
                resource.result?.let { resultState ->
                    if (resultState is ResultState.Success<*> && resultState.result is CCMessageStatus) {
                        val ccMessageStatus = (resultState.result as CCMessageStatus)
                        chnBinding.totalUnreadCount = ccMessageStatus.unreadCount.toString()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.channelManagerDisposed()
        chatViewModel.messageManagerDisposed()
        chatViewModel.userManagerDisposed()
        channelBinding = null
    }

    override fun onChannelClick(
        view: View, channelId: String, lastMessageCreatedAt: Long, userAvatar: String,
        username: String, itemName: String, itemPrice: String, itemImage: String
    ) {
        val action =
            ChannelFragmentDirections.chooseChannel(
                channelId,
                lastMessageCreatedAt,
                args.userId,
                itemPrice,
                itemName,
                itemImage,
                userAvatar,
                username
            )
        view.findNavController().navigate(action)
    }

    override fun onChannelOnlineStatus(view: View, sellerId: String) {
        chatViewModel.getOnlineStatus(sellerId)  {
            it
        }
    }

    private fun showChannelError() {
        channelBinding?.let {
            val errorBar = Snackbar.make(
                it.root,
                "Not channel list found!",
                Snackbar.LENGTH_LONG
            )
            errorBar.show()
        }
    }

    private fun showRequiredListIdDialog(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle("Please enter list Id that available in your company!")
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_list_id,
            null
        )
        builder.setView(dialogView)
        builder
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                val edtListId = dialogView.findViewById<EditText>(R.id.edt_list_id)
                sendDialogDataToActivity(edtListId.text.toString())
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sendDialogDataToActivity(listId: String) {
        chatViewModel.createChannel(listId, "Open Chat", "text")
    }
}