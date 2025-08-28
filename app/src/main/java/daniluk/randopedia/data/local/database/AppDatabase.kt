

package daniluk.randopedia.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RandomUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun randomUserDao(): RandomUserDao
}
