package handling.userHandling

import com.mongodb.client.MongoCollection
import handling.chatHandling.ChatDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.User
import models.UsersList
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.ne
import org.litote.kmongo.or
import org.slf4j.LoggerFactory

class UserDataManager(
    private val usersCollection: MongoCollection<User>,
    private val chatDataManager: ChatDataManager
) {
    private val logger = LoggerFactory.getLogger("user data manager")

    suspend fun signOnUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (isUserDetailsExists(user)) {
                    logger.warn("Attempt to sign on user with existing details: $user")
                    false
                } else {
                    usersCollection.insertOne(user)
                    true
                }
            } catch (e: Exception) {
                logger.error("Error signing on user: ${e.message}", e)
                false
            }
        }
    }

    private suspend fun isUserDetailsExists(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.findOne(
                    or(
                        User::name eq user.name,
                        User::port eq user.port,
                        User::ipAddress eq user.ipAddress
                    )
                ) != null
            } catch (e: Exception) {
                logger.error("Error checking user details: ${e.message}", e)
                false
            }
        }
    }

    suspend fun getUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.findOne(User::_id eq uid)
            } catch (e: Exception) {
                logger.error("Error retrieving user with uid $uid: ${e.message}", e)
                null
            }
        }
    }

    suspend fun getUsersList(): List<User> {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.find().toList()
            } catch (e: Exception) {
                logger.error("Error retrieving users list: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun getUsersListSorted(uid: String): UsersList {
        return withContext(Dispatchers.IO) {
            try {
                val userList = usersCollection.find(User::_id ne uid).toList()
                val chatList = chatDataManager.getAllChats(uid)
                val knownUserIds = chatList.flatMap { listOf(it.usersUid.first, it.usersUid.second) }.toSet()

                val knownUser = userList.filter { it._id in knownUserIds }
                val unknownUsers = userList.filter { it._id !in knownUserIds }

                UsersList(userList, unknownUsers, knownUser)
            } catch (e: Exception) {
                logger.error("Error retrieving sorted users list: ${e.message}", e)
                UsersList(emptyList(), emptyList(), emptyList())
            }
        }
    }

    suspend fun doesUserExist(uid: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                usersCollection.findOne(User::_id eq uid) != null
            } catch (e: Exception) {
                logger.error("Error checking if user exists: ${e.message}", e)
                false
            }
        }
    }

    suspend fun deleteUser(uid: String) {
        withContext(Dispatchers.IO) {
            try {
                usersCollection.deleteOne(User::_id eq uid)
            } catch (e: Exception) {
                logger.error("Error deleting user with uid $uid: ${e.message}", e)
            }
        }
    }
}
