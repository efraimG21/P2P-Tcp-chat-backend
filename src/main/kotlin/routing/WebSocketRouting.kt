package routing

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import models.WebSocketConnectionSession
import socketHandling.WebSocketManager


fun Route.webSocketRouting(webSocketManager: WebSocketManager) {
    route("/socket") {
        webSocket("/{uid?}") {
            try {
                val uid = call.parameters["uid"]
                if (uid == null) {
                    call.respond(HttpStatusCode.BadRequest, "error no uid provided")
                }

            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, "error in web socket: ${e}")
            }
        }
    }
}