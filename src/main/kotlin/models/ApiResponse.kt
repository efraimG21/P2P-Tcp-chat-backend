package models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val message: String,
    val content: String,
)
