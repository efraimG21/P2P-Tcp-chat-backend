package models

data class UsersList(
    val users: List<User>,
    val unknownUsers: List<User>,
    val knownUsers: List<User>,
)
