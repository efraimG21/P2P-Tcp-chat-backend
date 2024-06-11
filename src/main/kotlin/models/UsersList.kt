package models

import kotlinx.serialization.Serializable

@Serializable
data class UsersList(
    val users: List<User>,
    val unknownUsers: List<User>,
    val knownUsers: List<User>,
)
