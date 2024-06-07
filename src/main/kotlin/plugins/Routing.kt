package plugins

import handling.chatHandling.ChatDataManager
import com.mongodb.client.MongoCollection
import io.ktor.server.application.*
import io.ktor.server.routing.*
import models.Chat
import models.User
import routing.chatRouting
import routing.userRouting
import routing.webSocketRouting
import handling.socketHandling.WebSocketManager
import handling.userHandling.UserDataManager

fun Application.configureRouting(usersCollection: MongoCollection<User>, chatCollection: MongoCollection<Chat>) {
    val chatDataManager = ChatDataManager(chatCollection)
    val userDataManager = UserDataManager(usersCollection, chatDataManager)
    val webSocketManager = WebSocketManager(userDataManager, chatDataManager)

    routing {
        userRouting(userDataManager)
        chatRouting(chatDataManager, userDataManager)
        webSocketRouting(webSocketManager)
    }
}
