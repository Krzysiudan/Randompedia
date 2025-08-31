

package daniluk.randopedia.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room entity mirroring the displayed User model so we can persist the whole item on click.
 * Add a unique index on the business id to prevent duplicates and enable bookmark semantics.
 */
@Entity(indices = [Index(value = ["id"], unique = true)])
data class RandomUser(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val country: String,
    val city: String,
    val age: Int,
    val avatarUrl: String,
    val photoUrl: String
)

@Dao
interface RandomUserDao {
    @Query("SELECT * FROM randomuser ORDER BY uid DESC")
    fun getRandomUsers(): Flow<List<RandomUser>>

    // Flow of bookmarked IDs to overlay on the network-only paging list
    @Query("SELECT id FROM randomuser")
    fun getBookmarkedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRandomUser(item: RandomUser)

    @Query("DELETE FROM randomuser WHERE id = :id")
    suspend fun deleteById(id: String)
}
