package plugins

import com.mongodb.client.MongoCollection
import handling.chatHandling.ChatDataManager
import handling.socketHandling.WebSocketManager
import handling.userHandling.UserDataManager
import handling.usersListHandling.UsersListDataManager
import io.ktor.server.application.*
import io.ktor.server.routing.*
import models.Chat
import models.User
import routing.chatRouting
import routing.userListRouting
import routing.userRouting
import routing.webSocketRouting

fun Application.configureRouting(usersCollection: MongoCollection<User>, chatCollection: MongoCollection<Chat>) {
    val chatDataManager = ChatDataManager(chatCollection)
    val userDataManager = UserDataManager(usersCollection, chatDataManager)
    val usersListDataManager = UsersListDataManager(usersCollection, chatDataManager)
    val webSocketManager = WebSocketManager(userDataManager, chatDataManager)

    routing {
        userRouting(userDataManager)
        userListRouting(usersListDataManager)
        chatRouting(chatDataManager, userDataManager)
        webSocketRouting(webSocketManager)
    }
}
