package daniluk.randopedia.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import daniluk.randopedia.data.local.entity.BookmarkedUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedUserDao {
    @Query("SELECT * FROM bookmarkeduserentity ORDER BY uid DESC")
    fun getBookmarkedUsers(): Flow<List<BookmarkedUserEntity>>

    @Query("SELECT id FROM bookmarkeduserentity")
    fun getBookmarkedIds(): Flow<List<String>>

    @Query("DELETE FROM bookmarkeduserentity WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertBookmarkedUser(item: BookmarkedUserEntity)
}