

package daniluk.randopedia.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import daniluk.randopedia.data.local.entity.BookmarkedUserEntity

@Database(entities = [BookmarkedUserEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomUserDao(): BookmarkedUserDao
}
