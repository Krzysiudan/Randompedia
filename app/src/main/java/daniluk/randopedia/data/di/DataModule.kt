package daniluk.randopedia.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import daniluk.randopedia.data.RandomUserRepositoryImpl
import daniluk.randopedia.domain.RandomUserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsRandomUserRepository(
        randomUserRepository: RandomUserRepositoryImpl
    ): RandomUserRepository
}
