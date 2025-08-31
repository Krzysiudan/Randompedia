

package daniluk.randopedia.testdi

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.data.di.DataModule
import daniluk.randopedia.data.di.FakeRandomUserRepository

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeRandomUserRepository
    ): RandomUserRepository
}
