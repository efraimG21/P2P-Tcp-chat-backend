package routing


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import userHandling.UserDataManager

fun Route.userRouting(userDataManager: UserDataManager) {
    val logger = LoggerFactory.getLogger("userRouting")

    route("/user-handling") {
        get("/get/{uid?}") {}
        post("/sign-on") {}

        get("/user-list") {
            try {
                val userList = withContext(Dispatchers.IO) {
                    userDataManager.getUsersList()
                }
                call.respond(HttpStatusCode.OK, userList)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, "Error in getting user list: ${e.message}")
            }
        }
        get("/is-exists/{uid?}") {
            val uid = call.parameters["uid"]
            if (uid == null) {
                call.respond(HttpStatusCode.BadRequest, "uid missing")
                return@get
            }
            try {
                val userExists = withContext(Dispatchers.IO) {
                    userDataManager.doesUserExist(uid)
                }
                call.respond(HttpStatusCode.OK, userExists)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadGateway, "Error in checking if user exists: ${e.message}")
            }
        }
    }
}