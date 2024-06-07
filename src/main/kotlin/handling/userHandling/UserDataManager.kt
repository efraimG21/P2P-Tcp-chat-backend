package handling.userHandling

import com.mongodb.client.MongoCollection
import handling.chatHandling.ChatDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import models.UsersList
import org.litote.kmongo.*
import org.slf4j.LoggerFactory

class UserDataManager(private val usersCollection: MongoCollection<User>, private val chatDataManager: ChatDataManager) {
    private val logger = LoggerFactory.getLogger("user data manager")

    suspend fun signOnUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            if (isUserDetailsExists(user)) {
                logger.warn("Attempt to sign on user with existing details: $user")
                false
            } else {
                usersCollection.insertOne(user)
                true
            }
        }
    }

    private suspend fun isUserDetailsExists(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            usersCollection.findOne(or(User::name eq user.name, User::port eq user.port, User::ipAddress eq user.ipAddress)) != null
        }
    }

    suspend fun getUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            usersCollection.findOne(User::_id eq uid)
        }
    }

    suspend fun getUsersList(): List<User> {
        return withContext(Dispatchers.IO) {
            usersCollection.find().toList()
        }
    }

    suspend fun getUsersListSorted(uid: String): UsersList {
        return withContext(Dispatchers.IO) {
            val userList = usersCollection.find().toList()
            val chatList = chatDataManager.getAllChats(uid)
            val knownUserIds = chatList.flatMap { listOf(it.usersUid.first, it.usersUid.second) }.toSet()

            val knownUser = userList.filter { it._id in knownUserIds }
            val unknownUsers = userList.filter { it._id !in knownUserIds }

            UsersList(userList, knownUser, unknownUsers)
        }
    }

    suspend fun doesUserExist(uid: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.findOne(User::_id eq uid) != null
            } catch (e: Exception) {
                logger.error("Error occurred while checking if user exists: ${e.message}")
                false
            }
        }
    }

    suspend fun deleteUser(uid: String) {
        withContext(Dispatchers.IO) {
            usersCollection.deleteOne(User::_id eq uid);
        }
    }
}
