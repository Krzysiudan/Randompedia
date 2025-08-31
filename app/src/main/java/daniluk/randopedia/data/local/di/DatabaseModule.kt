

package daniluk.randopedia.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import daniluk.randopedia.data.local.database.AppDatabase
import daniluk.randopedia.data.local.database.RandomUserDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideRandomUserDao(appDatabase: AppDatabase): RandomUserDao {
        return appDatabase.randomUserDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "RandomUser"
        )
            // We can safely drop/refresh local cache when schema changes
            .fallbackToDestructiveMigration(true)
            .build()
    }
}
