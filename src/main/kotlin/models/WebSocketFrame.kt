package models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class WebSocketFrame (
    val typeOf: String, //'userLogIn' | 'userLogOut' | 'message' | 'sendMessage'  | 'messageReceived' | 'messageRead'
    @Contextual
    val frame: Any,
)

@Serializable
data class IncomingWebSocketFrame (
    val typeOf: String,
    val frame: MessageWebSocketFrame,
)

@Serializable
data class MessageWebSocketFrame(
    val chatUid: String,
    val receivedUid: String,
    val content: String?,
    val timeStamp: String?,
)

//@Serializable
//data class UpdateMessagesStatusWebSocketFrame (
//    val chatUid: String,
//    val senderUid: String,
//)