package daniluk.randopedia.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity mirroring the displayed User model so we can persist the whole item on click.
 * Add a unique index on the business id to prevent duplicates and enable bookmark semantics.
 */
@Entity(indices = [Index(value = ["id"], unique = true)])
data class BookmarkedUserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val country: String,
    val city: String,
    val avatarUrl: String,
    val photoUrl: String
)
