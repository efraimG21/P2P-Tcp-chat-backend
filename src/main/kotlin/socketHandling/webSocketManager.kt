package socketHandling

import chatHandling.ChatDataManager
import io.ktor.websocket.*
import userHandling.UserDataManager
import java.util.*

class WebSocketManager(private val userDataManager: UserDataManager, private val chatDataManager: ChatDataManager) {
    private val socketSessionsCollection = Collections.synchronizedMap<String, DefaultWebSocketSession>(LinkedHashMap())

    fun onStartConnection(uid: String, session: DefaultWebSocketSession) {
        if (!socketSessionsCollection.containsKey(uid))
        {
            socketSessionsCollection[uid] = session
            notification("User")
        }
    }

    private fun notification(typeOfFrame: String) {
    }
}