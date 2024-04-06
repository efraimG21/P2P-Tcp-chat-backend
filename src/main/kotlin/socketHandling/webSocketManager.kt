package socketHandling

import chatHandling.ChatDataManager
import io.ktor.websocket.*
import models.WebSocketConnectionSession
import userHandling.UserDataManager
import java.util.*

class WebSocketManager(private val userDataManager: UserDataManager, private val chatDataManager: ChatDataManager) {
    private val socketSessionsCollection = Collections.synchronizedMap<String, DefaultWebSocketSession>(LinkedHashMap())

    fun onStartConnection(connection: WebSocketConnectionSession) {
    }

}