package daniluk.randopedia.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val country: String,
    val city: String,
    val age: Int,
    val phone: String,
    val avatarUrl: String,
    val photoUrl: String
)