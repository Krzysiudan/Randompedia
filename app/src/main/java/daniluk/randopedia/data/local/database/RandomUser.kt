

package daniluk.randopedia.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class RandomUser(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface RandomUserDao {
    @Query("SELECT * FROM randomuser ORDER BY uid DESC LIMIT 10")
    fun getRandomUsers(): Flow<List<RandomUser>>

    @Insert
    suspend fun insertRandomUser(item: RandomUser)
}
