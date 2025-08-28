package daniluk.randopedia.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import daniluk.randopedia.data.RandomUserRepository
import daniluk.randopedia.data.DefaultRandomUserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsRandomUserRepository(
        randomUserRepository: DefaultRandomUserRepository
    ): RandomUserRepository
}

class FakeRandomUserRepository @Inject constructor() : RandomUserRepository {
    override val randomUsers: Flow<List<String>> = flowOf(fakeRandomUsers)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeRandomUsers = listOf("One", "Two", "Three")
