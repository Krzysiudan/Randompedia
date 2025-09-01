package daniluk.randopedia.ui.list

import androidx.paging.PagingData
import daniluk.randopedia.common.MainDispatcherRule
import daniluk.randopedia.domain.AppError
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import daniluk.randopedia.domain.userMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sampleUser = User(
        id = "1",
        fullName = "John Doe",
        email = "john@example.com",
        country = "USA",
        city = "NYC",
        avatarUrl = "",
        photoUrl = "",
        phone = ""
    )

    @Test
    fun onBookmarkClicked_removes_whenAlreadyBookmarked() = runTest {
        val repo = mockk<RandomUserRepository>()
        // Flows/state
        every { repo.userPagingFlow() } returns flowOf(PagingData.empty())
        every { repo.bookmarkedUserIds() } returns flowOf(setOf(sampleUser.id))
        every { repo.bookmarkedUsers() } returns flowOf(listOf(sampleUser))
        // Suspend methods
        coEvery { repo.removeBookmarkById(any()) } returns Unit
        coEvery { repo.addBookmark(any()) } returns Unit

        val vm = UsersListViewModel(repo)

        // Activate bookmarkedIds state before clicking
        val pagingJob = launch { vm.uiPagingFlow.collect { /* ignore */ } }
        runCurrent()

        vm.onBookmarkClicked(sampleUser)
        runCurrent()

        // Verify repository interactions
        coVerify(exactly = 1) { repo.removeBookmarkById(sampleUser.id) }
        coVerify(exactly = 0) { repo.addBookmark(any()) }

        pagingJob.cancel()
    }

    @Test
    fun bookmarkedUsers_emits_fromRepository() = runTest {
        val repo = mockk<RandomUserRepository>()
        every { repo.bookmarkedUsers() } returns flowOf(listOf(sampleUser))
        // Provide defaults for other calls that may be lazily created
        every { repo.bookmarkedUserIds() } returns flowOf(emptySet())
        every { repo.userPagingFlow() } returns flowOf(PagingData.empty())

        val vm = UsersListViewModel(repo)

        val list = vm.bookmarkedUsers.drop(1).first()
        assertEquals(1, list.size)
        assertEquals(sampleUser, list.first())
    }

    @Test
    fun emitsEvent_whenBookmarkedIdsFlowErrors() = runTest {
        val errorMessage = AppError.Storage().let { it.userMessage() }
        val repo = mockk<RandomUserRepository>()
        every { repo.userPagingFlow() } returns flowOf(PagingData.empty())
        every { repo.bookmarkedUserIds() } returns flow<Set<String>> { throw AppError.Storage() }
        every { repo.bookmarkedUsers() } returns flowOf(emptyList())

        val vm = UsersListViewModel(repo)

        val events = mutableListOf<UsersListViewModel.UiEvent>()
        val job = launch { vm.events.collect { events += it } }

        // Trigger subscription to bookmarkedIds
        val collectJob = launch { vm.uiPagingFlow.collect { /* ignore items */ } }
        runCurrent()

        assertTrue(events.isNotEmpty())
        val first = events.first() as UsersListViewModel.UiEvent.ShowMessage
        assertEquals(errorMessage, first.message)

        collectJob.cancel()
        job.cancel()
    }

    @Test
    fun emitsEvent_whenBookmarkedUsersFlowErrors() = runTest {
        val repo = mockk<RandomUserRepository>()
        every { repo.bookmarkedUserIds() } returns flowOf(emptySet())
        every { repo.bookmarkedUsers() } returns flow<List<User>> { throw AppError.Network() }
        every { repo.userPagingFlow() } returns flowOf(PagingData.empty())

        val vm = UsersListViewModel(repo)

        val events = mutableListOf<UsersListViewModel.UiEvent>()
        val job = launch { vm.events.collect { events += it } }

        val collectJob = launch { vm.bookmarkedUsers.collect { /* ignore */ } }
        runCurrent()

        assertTrue(events.isNotEmpty())
        val event = events.first() as UsersListViewModel.UiEvent.ShowMessage
        assertEquals(AppError.Network().userMessage(), event.message)

        collectJob.cancel()
        job.cancel()
    }
}
