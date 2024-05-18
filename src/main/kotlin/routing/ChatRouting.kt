package routing

import chatHandling.ChatDataManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.chatRouting(chatDataManager: ChatDataManager) {
    route("/chat") {
        get("/get/{uid1?}/{uid2?}") {
            val uid1 = call.parameters["uid1"]
            val uid2 = call.parameters["uid2"]
            if (uid1 == null || uid2 == null) {
                call.respond(HttpStatusCode.BadRequest, "uid missing")
                return@get
            }
            try {
                val chat = withContext(Dispatchers.IO) {
                    chatDataManager.getChat(uid1, uid2)
                }
                call.respond(HttpStatusCode.OK, chat)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, "error in get chat: $e")
            }
        }
    }
}