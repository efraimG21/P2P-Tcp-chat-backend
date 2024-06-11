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
            try {
                val uid = call.parameters["uid"]
                if (uid == null) {
                    call.respond(HttpStatusCode.BadRequest, "error no uid provided")
                    return@webSocket
                }
                webSocketManager.onStartConnection(uid.toString(), this)

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    webSocketManager.incomingFrame(uid, frame.readText())
                }

                webSocketManager.disconnect(uid, this)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, "error in web socket: ${e}")
            }
        }
    }
}