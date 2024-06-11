package handling.usersListHandling

import com.mongodb.client.MongoCollection
import handling.chatHandling.ChatDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import models.UsersList
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.ne
import org.slf4j.LoggerFactory

class UsersListDataManager (
    private val usersCollection: MongoCollection<User>,
    private val chatDataManager: ChatDataManager
) {
    private val logger = LoggerFactory.getLogger("users list data manager")

    suspend fun getUsersList(): List<User> {
        return withContext(Dispatchers.IO) {
            usersCollection.find().toList()
        }
    }

    suspend fun getUsersListSorted(uid: String): UsersList {
        return withContext(Dispatchers.IO) {
            val userList = usersCollection.find(User::_id ne uid).toList()
            val chatList = chatDataManager.getAllChats(uid)
            val knownUserIds = chatList.flatMap { listOf(it.usersUid.first, it.usersUid.second) }.toSet()

            val knownUser = userList.filter { it._id in knownUserIds }
            val unknownUsers = userList.filter { it._id !in knownUserIds }

            UsersList(userList, unknownUsers, knownUser)
        }
    }
}