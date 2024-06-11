package routing

import handling.usersListHandling.UsersListDataManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

fun Route.userListRouting(usersListDataManager: UsersListDataManager) {
    val logger = LoggerFactory.getLogger("UsersListRouting")

    route("/users-list") {
        get("") {
            try {
                val userList = withContext(Dispatchers.IO) {
                    usersListDataManager.getUsersList()
                }
                call.respond(HttpStatusCode.OK, userList)
            } catch (e: Exception) {
                logger.error("Error retrieving user list: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving user list: ${e.message}")
            }
        }

        get("/sorted-user-list/{uid}") {
            try {
                val uid = call.parameters["uid"]
                if (uid == null) {
                    logger.warn("get called without uid parameter")
                    call.respond(HttpStatusCode.BadRequest, "uid missing")
                    return@get
                }
                val sortedList = withContext(Dispatchers.IO) {
                    usersListDataManager.getUsersListSorted(uid)
                }
                call.respond(HttpStatusCode.OK, sortedList)
            } catch (e: Exception) {
                logger.error("Error retrieving sorted user list: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving sorted user list: ${e.message}")
            }
        }
    }
}