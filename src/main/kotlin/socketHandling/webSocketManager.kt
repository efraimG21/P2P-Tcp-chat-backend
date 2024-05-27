package socketHandling

import chatHandling.ChatDataManager
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.json
import userHandling.UserDataManager
import java.util.*

class WebSocketManager(private val userDataManager: UserDataManager, private val chatDataManager: ChatDataManager) {
    private val socketSessionsCollection = Collections.synchronizedMap<String, DefaultWebSocketSession>(LinkedHashMap())

    suspend fun onStartConnection(uid: String, session: DefaultWebSocketSession) {
        if (!socketSessionsCollection.containsKey(uid))
        {
            socketSessionsCollection[uid] = session
            notification("User", uid)
        }
    }

    private suspend fun notification(typeOfFrame: String, content: Any) {
        this.socketSessionsCollection.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    it.value.send(Frame.Text("$typeOfFrame: $content").json)
                } catch (e: Exception) {
                    println("Error sending message: ${e.message}")
                }
            }
        }
    }
}