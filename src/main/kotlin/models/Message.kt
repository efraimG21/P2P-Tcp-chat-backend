package models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Message(
    val senderUid: String,
    val content: String,
    @Contextual
    val timeStamp: String,
    val status: String, //'Sent' | 'Received' | 'Read'
)

