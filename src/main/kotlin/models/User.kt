package models

import java.rmi.server.UID

data class User(
    val uid: UID,
    val name: String,
    val ipAddress: String,
    val port: String,
)
