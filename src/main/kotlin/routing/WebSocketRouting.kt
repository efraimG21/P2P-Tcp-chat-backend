package routing

import handling.socketHandling.WebSocketManager
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*


fun Route.webSocketRouting(webSocketManager: WebSocketManager) {
    route("/socket") {
        webSocket("/{uid?}") {
            val uid = call.parameters["uid"]
            if (uid == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No UID provided"))
                return@webSocket
            }
            try {
                webSocketManager.onStartConnection(uid, this)

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    webSocketManager.incomingFrame(uid, frame.readText())
                }
            } catch (e: Exception) {
                close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Error in WebSocket: ${e.message}"))
            } finally {
                webSocketManager.disconnect(uid, this)
            }
        }
    }
}