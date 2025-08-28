

package daniluk.randopedia.ui.randomuser


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import daniluk.randopedia.data.RandomUserRepository

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class RandomUserViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = RandomUserViewModel(FakeRandomUserRepository())
        assertEquals(viewModel.uiState.first(), RandomUserUiState.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = RandomUserViewModel(FakeRandomUserRepository())
        assertEquals(viewModel.uiState.first(), RandomUserUiState.Loading)
    }
}

private class FakeRandomUserRepository : RandomUserRepository {

    private val data = mutableListOf<String>()

    override val randomUsers: Flow<List<String>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(name: String) {
        data.add(0, name)
    }
}
