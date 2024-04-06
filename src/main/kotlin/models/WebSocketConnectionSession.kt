package models

import io.ktor.websocket.*
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketConnectionSession(
    val uid: String,
    val session: DefaultWebSocketSession
)
