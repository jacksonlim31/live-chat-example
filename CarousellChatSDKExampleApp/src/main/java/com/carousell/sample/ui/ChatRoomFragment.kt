package com.carousell.sample.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.carousell.chat.models.*
import com.carousell.chat.remotes.common.ResultState
import com.carousell.sample.R
import com.carousell.sample.configs.Constant
import com.carousell.sample.databinding.FragmentChatBinding
import com.carousell.sample.model.Action
import com.carousell.sample.model.Resource
import com.carousell.sample.model.User
import com.carousell.sample.ui.adapters.ChatRoomAdapter
import com.carousell.sample.ui.interfaces.MessageActions
import com.carousell.sample.utils.ChatUtil
import com.carousell.sample.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ChatRoomFragment : Fragment(R.layout.fragment_chat), MessageActions {
    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChatRoomFragmentArgs by navArgs()

    private var chatRoomBinding: FragmentChatBinding? = null
    private val binding get() = chatRoomBinding!!
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var mainHandler: Handler
    private var messageList = arrayListOf<CCMessage>()
    private val imageList = arrayListOf<String>()
    private var uniqueUserIds = arrayListOf<String>()
    private val isSendOnlineStatus = false
    private var userId = ""
    private val getStatus = object : Runnable {
        override fun run() {
            chatRoomStatus()
            mainHandler.postDelayed(this, 8000)
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(getStatus)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(getStatus)
        if (!chatViewModel.isConnected()) {
            chatViewModel.reconnectSocket {
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        chatViewModel.messageManagerDisposed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatViewModel.disconnectSocket()
        chatViewModel.channelManagerDisposed()
        chatViewModel.messageManagerDisposed()
        chatViewModel.userManagerDisposed()
        chatRoomBinding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatRoomBinding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatRoomBinding?.let { chRmBinding ->
            mainHandler = Handler(Looper.getMainLooper())
            ChatUtil.retrieveClassInSharedPreferences(
                view.context,
                Constant.USER_DATA,
                User::class.java,
                ""
            )
                .let { user ->
                    this.userId = user.profile.accountId.toString()
                    val linearLayout =
                        LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    chatRoomAdapter = ChatRoomAdapter(this, userId)
                    chRmBinding.rvChatRoom.adapter = chatRoomAdapter
                    chRmBinding.rvChatRoom.layoutManager = linearLayout
                    chRmBinding.username = args.userName
                    chRmBinding.userAvatar = args.userAvatar
                    getChatMessage()
                    chatViewModel.onMessage()
                    chatViewModel.onTypingStatus()
                }
            setMessageFieldListener()

            chRmBinding.btnSubmit.setOnClickListener {
                val message = chRmBinding.edtMessage.text.toString()
                chatViewModel.sendMessage(args.channelId, message, imageList)
                chRmBinding.edtMessage.text.clear()
                ChatUtil.hideSoftKeyboard(activity)
            }

            chRmBinding.imageSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    imageList.add("https://static.toiimg.com/photo/msid-58515713,width-96,height-65.cms")
                } else {
                    imageList.clear()
                }
            }

            chRmBinding.btnArchive.setOnClickListener {
                showArchiveConfirmation(it)
            }

            chRmBinding.btnBlock.setOnClickListener {
                showBlockConfirmation(it, "Are you want to block this user?", true)
            }

            chRmBinding.btnUnblock.setOnClickListener {
                showBlockConfirmation(it, "Are you want to unblock this user?", false)
            }
        }
    }

    private fun showArchiveConfirmation(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle("Are you want to archive this channel?")
        builder
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                chatViewModel.archiveChannel(args.channelId, true)
            }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showBlockConfirmation(view: View, title: String, isBlocked: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder.setTitle(title)
        builder
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                setBlockAction(isBlocked)
            }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun setMessageFieldListener() {
        chatRoomBinding?.let { chRmBinding ->
            chRmBinding.edtMessage.setOnClickListener {
                chRmBinding.rvChatRoom.smoothScrollToPosition(messageList.size - 1)
            }

            chRmBinding.edtMessage.addTextChangedListener(object : TextWatcher {
                private var timer: Timer = Timer()
                private val DELAY: Long = 300

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(phrase: Editable?) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                if (chatViewModel.isConnected() && uniqueUserIds.isNotEmpty()) {
                                    chatViewModel.sendTypingStatus(args.channelId, uniqueUserIds)
                                }
                            }
                        },
                        DELAY
                    )
                }
            })
        }
    }

    private fun getChatMessage() {
        val lastMessageCreatedAtLong = args.lastMessageCreatedAt + 86400000
        val messageAfterCreatedAtLong = args.lastMessageCreatedAt - 86400000
        chatViewModel.getChannelMessage(
            args.channelId,
            Ordering.ASC,
            50,
            TimeEvent.BEFORE,
            lastMessageCreatedAtLong.toString()
        )

        chatViewModel.getChannelMessageInDB(args.channelId, Ordering.ASC)

        chatViewModel.getChannelMessageChanges(
            args.channelId,
            Ordering.ASC,
            50,
            TimeEvent.AFTER,
            messageAfterCreatedAtLong.toString()
        )

        chatViewModel.messageLiveData.observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Loading -> {
                    chatRoomBinding?.progressCircular?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    chatRoomBinding?.progressCircular?.visibility = View.GONE
                    resource.result?.let { resultState ->
                        when (resultState) {
                            is ResultState.Success<*> -> {
                                when (resource.action) {
                                    Action.RETRIEVE_CHANNEL_MESSAGES -> {
                                        if (resultState.result is CCMessageList) {
                                            messageList =
                                                (resultState.result as CCMessageList).messageList
                                            chatRoomAdapter.submitList(messageList)
                                            chatRoomBinding?.rvChatRoom?.scrollToPosition(
                                                messageList.size - 1
                                            )
                                            executeRoomStatusUpdates()
                                        }
                                    }
                                    Action.SEND_MESSAGES_VIA_SOCKET -> {
                                        if (resultState.result is CCMessage) {
                                            val ccMessage = (resultState.result as CCMessage)
                                            if (ccMessage.channelId.isNotBlank()) {
                                                messageList.add(ccMessage)
                                            }
                                            val finalMessageList =
                                                ChatUtil.removeDuplicates(messageList)
                                            chatRoomAdapter.submitList(finalMessageList)
                                            chatRoomBinding?.rvChatRoom?.scrollToPosition(
                                                finalMessageList.size - 1
                                            )
                                        }
                                    }
                                    Action.ONLINE_STATUS -> {
                                        if (resultState.result is CCOnlineStatusList) {
                                            val ccOnlineStatusList =
                                                (resultState.result as CCOnlineStatusList)
                                            for (i in 0 until ccOnlineStatusList.status.size) {
                                                val ccOnlineStatus = ccOnlineStatusList.status[i]
                                                if (!ccOnlineStatus.userId.equals(
                                                        args.userId,
                                                        true
                                                    )
                                                ) {
                                                    if (ccOnlineStatus.isOnline) {
                                                        chatRoomBinding?.txtStatus?.visibility =
                                                            View.VISIBLE
                                                    } else {
                                                        chatRoomBinding?.txtStatus?.visibility =
                                                            View.GONE
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Action.TYPING_STATUS -> {
                                        if (resultState.result is CCTypingStatus) {
                                            val ccTypingStatus =
                                                (resultState.result as CCTypingStatus)
                                            if (!args.userId.equals(
                                                    ccTypingStatus.userId,
                                                    true
                                                ) && args.channelId.equals(args.channelId, true)
                                            ) {
                                                chatRoomBinding?.txtTyping?.visibility =
                                                    View.VISIBLE
                                                lifecycleScope.launch {
                                                    delay(8000)
                                                    chatRoomBinding?.txtTyping?.visibility =
                                                        View.GONE
                                                }
                                            }
                                        }
                                    }
                                    Action.ARCHIVE_CHANNEL -> {
                                        if (resultState.result is Any) {
                                            Log.d("Archive channel:", "result")
                                        }
                                    }
                                    Action.RETRIEVE_CHANNEL_MESSAGES_CHANGE -> {
                                        if (resultState.result is CCMessageList) {
                                            val messageData =
                                                (resultState.result as CCMessageList).messageList
                                        }
                                    }
                                    else -> {
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    chatRoomBinding?.progressCircular?.visibility = View.GONE
                }
                is Resource.UnexpectedError -> {
                    chatRoomBinding?.progressCircular?.visibility = View.GONE
                }
                else -> {
                    chatRoomBinding?.progressCircular?.visibility = View.GONE
                }
            }
        })


    }

    private fun setBlockAction(isBlocked: Boolean) {
        chatRoomBinding?.let {
            chatViewModel.getChannelDetails(args.channelId)
            chatViewModel.channelLiveData.observe(viewLifecycleOwner, { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        it.progressCircular.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        it.progressCircular.visibility = View.GONE
                        resource.result?.let { resultState ->
                            when (resultState) {
                                is ResultState.Success<*> -> {
                                    when (resource.action) {
                                        Action.RETRIEVE_CHANNEL_DETAILS -> {
                                            if (resultState.result is CCChannelInfo<*, *>) {
                                                val channelInfo = (resultState.result as CCChannelInfo<*, *>)
                                                val channelDetails = channelInfo.channel as CCChannel
                                                if (channelDetails.sellerId.isNotBlank()) {
                                                    val blockList = arrayListOf<String>()
                                                    val unblockList = arrayListOf<String>()
                                                    if (isBlocked) {
                                                        blockList.add(channelDetails.sellerId)
                                                    } else {
                                                        unblockList.add(channelDetails.sellerId)
                                                    }
                                                    chatViewModel.blockUser(blockList, unblockList)
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    it.progressCircular.visibility = View.GONE
                                }
                            }
                        }
                    }
                    else -> {
                        it.progressCircular.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun executeRoomStatusUpdates() {
        if (!isSendOnlineStatus) {
            uniqueUserIds = filterUniqueUserIds(messageList)
            mainHandler.post(getStatus)
            lifecycleScope.launch {
                delay(8000)
                chatViewModel.sendReadStatus(args.channelId, uniqueUserIds)
            }
        }
    }

    private fun filterUniqueUserIds(ccMessageList: ArrayList<CCMessage>): ArrayList<String> {
        val uniqueUserIds = arrayListOf<String>()
        for (ccMessage in ccMessageList) {
            if (!uniqueUserIds.contains(ccMessage.senderId)) {
                uniqueUserIds.add(ccMessage.senderId)
            }
        }

        if (!uniqueUserIds.contains(args.userId)) {
            uniqueUserIds.add(args.userId)
        }
        return uniqueUserIds
    }

    private fun chatRoomStatus() {
        if (chatViewModel.isConnected() && uniqueUserIds.isNotEmpty()) {
            chatViewModel.sendOnlineStatus(uniqueUserIds)
        }
    }

    override fun onMessageClicked(view: View, position: Int, ccMessage: CCMessage) {
        chatViewModel.updateMessageViaSocket(
            ccMessage.channelId,
            "Edit Message",
            ccMessage.type,
            ccMessage.createdAt
        )
    }

    override fun onMessageLongClicked(view: View, position: Int, ccMessage: CCMessage) {
        //chatViewModel.deleteMessageViaSocket(ccMessage.channelId, ccMessage.createdAt)
        chatViewModel.editMessage(
            ccMessage.channelId,
            "Edit content",
            MessageAction.EDIT,
            ccMessage.createdAt
        )
    }
}