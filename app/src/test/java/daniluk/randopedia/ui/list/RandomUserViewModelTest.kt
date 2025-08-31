

package daniluk.randopedia.ui.list


import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class RandomUserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @Test
    fun onBookmarkClicked_insertsWithoutError() = runTest {
        val repo = FakeRandomUserRepository()
        val viewModel = UsersListViewModel(repo)
        viewModel.onBookmarkClicked(
            User(
                id = "1",
                fullName = "John Doe",
                email = "john@example.com",
                country = "USA",
                city = "NYC",
                age = 30,
                avatarUrl = "",
                photoUrl = "",
                phone = ""
            )
        )
        runCurrent()
        assertEquals(1, repo.savedUsers.size)
    }
}

private class FakeRandomUserRepository : RandomUserRepository {

    val savedUsers = mutableListOf<User>()

    override fun userPagingFlow(): Flow<PagingData<User>> =
        flowOf(PagingData.empty())

    override suspend fun addBookmark(user: User) {
        savedUsers.add(user)
    }

    override fun bookmarkedUserIds(): Flow<Set<String>> = flowOf(savedUsers.map { it.id }.toSet())

    override suspend fun removeBookmarkById(id: String) {
        savedUsers.removeAll { it.id == id }
    }

    override fun bookmarkedUsers(): Flow<List<User>> = flowOf(savedUsers.toList())
}


class MainDispatcherRule : TestWatcher() {
    private val dispatcher = StandardTestDispatcher()
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
