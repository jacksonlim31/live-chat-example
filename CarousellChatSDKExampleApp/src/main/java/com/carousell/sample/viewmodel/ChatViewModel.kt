package com.carousell.sample.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.carousell.chat.managers.ChatManager
import com.carousell.chat.models.*
import com.carousell.chat.remotes.common.ResultState
import com.carousell.chat.socket.SocketStatus
import com.carousell.sample.model.Action
import com.carousell.sample.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val userManager = ChatManager.createUserManager(context)
    private val channelManager = ChatManager.createChannelManager(context)
    private val messageManager = ChatManager.createMessageManager(context)
    private val connectionManager = ChatManager.createConnectionManager(context)
    val channelLiveData: MutableLiveData<Resource<ResultState>> = MutableLiveData()
    val messageLiveData: MutableLiveData<Resource<ResultState>> = MutableLiveData()

    fun getChannelList(offset: Int, limit: Int, role: String?) {
        channelLiveData.postValue(Resource.Loading(Action.RETRIEVE_CHANNEL_LIST))
        channelManager?.let {
            it.getChannels(offset, limit, role) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        channelLiveData.postValue(
                            Resource.Success(
                                Action.RETRIEVE_CHANNEL_LIST,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        channelLiveData.postValue(
                            Resource.Error(
                                Action.RETRIEVE_CHANNEL_LIST,
                                rsState
                            )
                        )
                    }
                    else -> {
                        channelLiveData.postValue(Resource.UnexpectedError(Action.RETRIEVE_CHANNEL_LIST))
                    }
                }
            }
        }
    }

    fun createChannel(listId: String, message: String, messageType: String) {
        channelLiveData.postValue(Resource.Loading(Action.CREATE_CHANNEL))
        channelManager?.let {
            it.createChannel(listId, message, messageType) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        channelLiveData.postValue(Resource.Success(Action.CREATE_CHANNEL, rsState))
                    }
                    is ResultState.Error -> {
                        channelLiveData.postValue(Resource.Error(Action.CREATE_CHANNEL, rsState))
                    }
                    else -> {
                        channelLiveData.postValue(Resource.UnexpectedError(Action.CREATE_CHANNEL))
                    }
                }
            }
        }
    }

    fun getChannelDetails(channelId: String) {
        channelLiveData.postValue(Resource.Loading(Action.RETRIEVE_CHANNEL_DETAILS))
        channelManager?.let {
            it.getChannelDetails(channelId) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        channelLiveData.postValue(
                            Resource.Success(
                                Action.RETRIEVE_CHANNEL_DETAILS,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        channelLiveData.postValue(
                            Resource.Error(
                                Action.RETRIEVE_CHANNEL_DETAILS,
                                rsState
                            )
                        )
                    }
                    else -> {
                        channelLiveData.postValue(Resource.UnexpectedError(Action.RETRIEVE_CHANNEL_DETAILS))
                    }
                }
            }
        }
    }

    fun archiveChannel(
        channelId: String,
        isHidden: Boolean
    ) {
        messageLiveData.postValue(Resource.Loading(Action.ARCHIVE_CHANNEL))
        channelManager?.let {
            it.archiveChannel(channelId, isHidden) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        messageLiveData.postValue(
                            Resource.Success(
                                Action.ARCHIVE_CHANNEL,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.ARCHIVE_CHANNEL,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.ARCHIVE_CHANNEL))
                    }
                }
            }
        }
    }

    fun blockUser(
        blockUsers: ArrayList<String>,
        unblockUsers: ArrayList<String>
    ) {
        messageLiveData.postValue(Resource.Loading(Action.BLOCK_USER))
        userManager?.let {
            if (blockUsers.isNotEmpty() || unblockUsers.isNotEmpty()) {
                it.blockUser(blockUsers, unblockUsers) { rsState ->
                    when (rsState) {
                        is ResultState.Success<*> -> {
                            messageLiveData.postValue(
                                Resource.Success(
                                    Action.BLOCK_USER,
                                    rsState
                                )
                            )
                        }
                        is ResultState.Error -> {
                            messageLiveData.postValue(
                                Resource.Error(
                                    Action.BLOCK_USER,
                                    rsState
                                )
                            )
                        }
                        else -> {
                            messageLiveData.postValue(Resource.UnexpectedError(Action.BLOCK_USER))
                        }
                    }
                }
            }
        }
    }

    fun getBlockAndBanInfo() {
        messageLiveData.postValue(Resource.Loading(Action.GET_BAN_BLOCK_INFO))
        userManager?.let {
            it.getUserBanAndBlockDetails { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        messageLiveData.postValue(
                            Resource.Success(
                                Action.GET_BAN_BLOCK_INFO,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.GET_BAN_BLOCK_INFO,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.GET_BAN_BLOCK_INFO))
                    }
                }

            }
        }
    }

    fun getChannelMessage(
        channelId: String,
        order: Ordering,
        limit: Int,
        timeEvent: TimeEvent,
        time: String
    ) {
        messageLiveData.postValue(Resource.Loading(Action.RETRIEVE_CHANNEL_MESSAGES))
        messageManager?.let {
            it.getMessages(channelId, order, limit, timeEvent, time) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        messageLiveData.postValue(
                            Resource.Success(
                                Action.RETRIEVE_CHANNEL_MESSAGES,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.RETRIEVE_CHANNEL_MESSAGES,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.RETRIEVE_CHANNEL_MESSAGES))
                    }
                }
            }
        }
    }

    fun getChannelMessageInDB(channelId: String, order: Ordering) {
        messageManager?.let {
            it.getMessageInDB(channelId, order) {

            }
        }
    }

    fun getChannelMessageChanges(
        channelId: String,
        order: Ordering,
        limit: Int,
        timeEvent: TimeEvent,
        time: String
    ) {
        messageLiveData.postValue(Resource.Loading(Action.RETRIEVE_CHANNEL_MESSAGES_CHANGE))
        messageManager?.let {
            it.getMessageChanges(channelId, order, limit, timeEvent, time) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        messageLiveData.postValue(
                            Resource.Success(
                                Action.RETRIEVE_CHANNEL_MESSAGES_CHANGE,
                                rsState
                            )
                        )
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.RETRIEVE_CHANNEL_MESSAGES_CHANGE,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.RETRIEVE_CHANNEL_MESSAGES_CHANGE))
                    }
                }
            }
        }
    }

    fun reconnectSocket(onCompleteCallback: (socketStatus: SocketStatus) -> Unit) {
        connectionManager?.let {
            it.forceReconnect(context) { socketStatus ->
                onCompleteCallback(socketStatus)
            }
        }
    }

    fun disconnectSocket(): Boolean {
        connectionManager?.let {
            return it.disconnect()
        }
        return false
    }

    fun connectSocket(context: Context, onCompleteCallback: (socketStatus: SocketStatus) -> Unit) {
        connectionManager?.let {
            it.connect(context) { socketStatus ->
                onCompleteCallback(socketStatus)
            }
        }
    }

    fun isConnected(): Boolean {
        connectionManager?.let {
            return it.isConnected()
        }
        return false
    }

    fun onMessage() {
        messageManager?.let {
            it.onMessage { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCMessage) {
                            messageLiveData.postValue(
                                Resource.Success(
                                    Action.SEND_MESSAGES_VIA_SOCKET,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.SEND_MESSAGES_VIA_SOCKET,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.SEND_MESSAGES_VIA_SOCKET))
                    }
                }
            }
        }
    }

    fun sendMessage(channelId: String, message: String, imageList: ArrayList<String>, clientGenId: Long? = null) {
        messageManager?.let {
            if (isConnected()) {
                sendMessageViaSocket(channelId, message, imageList, clientGenId)
            } else {
                sendMessageViaAPI(channelId, message, imageList, clientGenId)
            }
        }
    }

    fun sendOnlineStatus(userIds: ArrayList<String>) {
        messageManager?.let {
            it.sendOnlineStatus(userIds) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCOnlineStatusList) {
                            messageLiveData.postValue(
                                Resource.Success(
                                    Action.ONLINE_STATUS,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.ONLINE_STATUS,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.ONLINE_STATUS))
                    }
                }
            }
        }
    }

    fun sendTypingStatus(channelId: String, userIds: ArrayList<String>) {
        messageManager?.let {
            it.sendTypingStatus(channelId, userIds)
        }
    }

    fun onTypingStatus() {
        messageManager?.let {
            it.onTypingStatus { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCTypingStatus) {
                            messageLiveData.postValue(
                                Resource.Success(
                                    Action.TYPING_STATUS,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.TYPING_STATUS,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.TYPING_STATUS))
                    }
                }
            }
        }
    }

    fun sendReadStatus(channelId: String, userIds: ArrayList<String>) {
        messageManager?.let {
            it.sendReadStatus(channelId, userIds)
        }
    }

    fun onReadStatus() {
        messageManager?.let {
            it.onReadStatus { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCReadStatus) {
                            channelLiveData.postValue(
                                Resource.Success(
                                    Action.READ_STATUS,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        channelLiveData.postValue(
                            Resource.Error(
                                Action.READ_STATUS,
                                rsState
                            )
                        )
                    }
                    else -> {
                        channelLiveData.postValue(Resource.UnexpectedError(Action.READ_STATUS))
                    }
                }
            }
        }
    }

    fun getOnlineStatus(
        userId: String,
        onCompleteCallback: (resource: Resource<ResultState>) -> Unit
    ) {
        messageManager?.let {
            it.getOnlineStatusViaAPI(userId) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCOnlineStatus) {
                            onCompleteCallback(
                                Resource.Success(
                                    Action.USER_ONLINE_STATUS,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        onCompleteCallback(
                            Resource.Error(
                                Action.USER_ONLINE_STATUS,
                                rsState
                            )
                        )
                    }
                    else -> {
                        onCompleteCallback(Resource.UnexpectedError(Action.USER_ONLINE_STATUS))
                    }
                }
            }
        }
    }

    fun getTotalUnreadCount(onCompleteCallback: (resource: Resource<ResultState>) -> Unit) {
        messageManager?.let {
            it.getTotalUnreadCount { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCMessageStatus) {
                            onCompleteCallback(
                                Resource.Success(
                                    Action.TOTAL_UNREAD_COUNT,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        onCompleteCallback(
                            Resource.Error(
                                Action.TOTAL_UNREAD_COUNT,
                                rsState
                            )
                        )
                    }
                    else -> {
                        onCompleteCallback(Resource.UnexpectedError(Action.TOTAL_UNREAD_COUNT))
                    }
                }
            }
        }
    }

    fun updateMessageViaSocket(
        channelId: String,
        message: String,
        messageType: String,
        createdAt: Long
    ) {
        messageManager?.let {
            it.updateMessageViaSocket(channelId, message, messageType, createdAt)
        }
    }

    fun deleteMessageViaSocket(channelId: String, createdAt: Long) {
        messageManager?.let {
            it.deleteMessageViaSocket(channelId, createdAt)
        }
    }

    private fun sendMessageViaSocket(
        channelId: String,
        message: String,
        imageList: ArrayList<String>,
        clientGenId: Long? = null
    ) {
        messageManager?.let {
            it.sendMessageViaSocket(channelId, message, imageList, clientGenId) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        if (rsState.result is CCMessage) {
                            messageLiveData.postValue(
                                Resource.Success(
                                    Action.SEND_MESSAGES_VIA_SOCKET,
                                    rsState
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.SEND_MESSAGES_VIA_SOCKET,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.SEND_MESSAGES_VIA_SOCKET))
                    }
                }
            }
        }
    }

    private fun sendMessageViaAPI(
        channelId: String,
        message: String,
        imageList: ArrayList<String>,
        clientGenId: Long? = null
    ) {
        messageManager?.let {
            it.sendMessageViaAPI(channelId, message, imageList, clientGenId) { rsState ->
                when (rsState) {
                    is ResultState.Success<*> -> {
                        messageLiveData.postValue(
                            Resource.Success(Action.SEND_MESSAGES_VIA_API, rsState)
                        )
                    }
                    is ResultState.Error -> {
                        messageLiveData.postValue(
                            Resource.Error(
                                Action.SEND_MESSAGES_VIA_API,
                                rsState
                            )
                        )
                    }
                    else -> {
                        messageLiveData.postValue(Resource.UnexpectedError(Action.SEND_MESSAGES_VIA_API))
                    }
                }
            }
        }
    }

    fun editMessage(
        channelId: String,
        message: String,
        action: MessageAction,
        messageCreatedAt: Long
    ) {
        messageManager?.let {
            it.editMessageViaAPI(channelId, message, action, messageCreatedAt) {

            }
        }
    }

    fun channelManagerDisposed() {
        channelManager?.dispose()
    }

    fun messageManagerDisposed() {
        messageManager?.dispose()
    }

    fun userManagerDisposed() {
        userManager?.dispose()
    }
}