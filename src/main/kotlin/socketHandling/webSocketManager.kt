package socketHandling

import chatHandling.ChatDataManager
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import models.WebSocketFrame
import org.litote.kmongo.json
import org.slf4j.LoggerFactory
import userHandling.UserDataManager
import java.util.*

class WebSocketManager(private val userDataManager: UserDataManager, private val chatDataManager: ChatDataManager) {
    private val socketSessionsCollection = Collections.synchronizedMap<String, DefaultWebSocketSession>(LinkedHashMap())
    private val logger = LoggerFactory.getLogger("WebSocket manager")
    private var currentUserUID : String? = null

    suspend fun onStartConnection(uid: String, session: DefaultWebSocketSession) {
        val user = userDataManager.getUser(uid)

        if (!socketSessionsCollection.containsKey(uid) && user != null)
        {

            currentUserUID = uid
            socketSessionsCollection[uid] = session
            notification("userLogIn", user)

        } else {
            session.close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "UID already connected"))
        }
    }

    suspend fun incomingFrame(uid: String, frame: String) {
        try {
            val webSocketFrame = Json.decodeFromString<WebSocketFrame>(frame)
            when (webSocketFrame.typeOf) {
                "sendMessage" -> {}
                "messageReceived", "messageRead" -> {}
            }

        } catch (e: Exception) {
            logger.error("Error processing frame: ${e.message}")
        }
    }

    suspend fun disconnect(uid: String, session: DefaultWebSocketSession) {
        withContext(Dispatchers.IO) {
            session.close()
            socketSessionsCollection.remove(uid)
            logger.info("Disconnected session for UID: $uid")
//            userDataManager.deleteUser(uid)
            notification("userLogOut", uid)
        }

    }

    private suspend fun notification(message: String ,frame: Any) {
        this.socketSessionsCollection.forEach { (uid, session) ->
            if (uid != currentUserUID) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        session.send(Frame.Text(WebSocketFrame(message, frame).json))
                    } catch (e: Exception) {
                        logger.error("Error sending message: ${e.message}")
                    }
                }
            }
        }
    }
}