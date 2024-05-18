package routing

import chatHandling.ChatDataManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import org.slf4j.LoggerFactory
import userHandling.UserDataManager

val logger = LoggerFactory.getLogger("chatRouting")

fun Route.chatRouting(chatDataManager: ChatDataManager, userDataManager: UserDataManager) {
    route("/chat") {
        get("/get/{uid1?}/{uid2?}") {
            val uid1 = call.parameters["uid1"]
            val uid2 = call.parameters["uid2"]

            if (uid1 == null || uid2 == null) {
                call.respond(HttpStatusCode.BadRequest, "uid missing")
                return@get
            }
            if (uid1 == uid2) {
                call.respond(HttpStatusCode.BadRequest, "uid same")
                return@get
            }
            try {
                val userExists = withContext(Dispatchers.IO) {
                    userDataManager.doesUserExist(uid1) && userDataManager.doesUserExist(uid2)
                }
                if (!userExists) {
                    call.respond(HttpStatusCode.NotFound, "One or both users do not exist")
                    return@get
                }
                val chat = withContext(Dispatchers.IO) {
                    chatDataManager.getChat(uid1, uid2)
                }
                call.respond(HttpStatusCode.OK, chat)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving chat: ${e.localizedMessage}")
            }
        }

    }
}