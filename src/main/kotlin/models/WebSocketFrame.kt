package models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketFrame(
    val typeOf: String, // 'userLogIn' | 'userLogOut' | 'sendMessage' | 'messageReceived' | 'messageRead'
    @Contextual
    val content: Any,
)

//@Serializable
//sealed class FrameContent {
//
//    @Serializable
//    @kotlinx.serialization.SerialName("userContent")
//    data class UserContent(val user: User) : FrameContent()
//
//
//    @Serializable
//    @kotlinx.serialization.SerialName("stringContent")
//    data class StringContent(val content: String) : FrameContent()
//
//
//    @Serializable
//    @kotlinx.serialization.SerialName("frameMessage")
//    data class FrameMessageSocket(
//        val chatUID: String,
//        val uidReceived: String,
//        val content: String
//    ) : FrameContent()
//}
