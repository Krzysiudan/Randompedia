package daniluk.randopedia.data.local

import daniluk.randopedia.data.local.database.BookmarkedUserDao
import daniluk.randopedia.data.local.entity.BookmarkedUserEntity
import daniluk.randopedia.domain.AppError
import daniluk.randopedia.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksLocalDataSourceTest {

    private fun sampleUser(id: String = "u1") = User(
        id = id,
        fullName = "John Doe",
        email = "john@example.com",
        country = "USA",
        city = "NYC",
        avatarUrl = "a",
        photoUrl = "p",
        phone = "123"
    )

    private fun sampleEntity(id: String = "u1") = BookmarkedUserEntity(
        uid = 0,
        id = id,
        fullName = "John Doe",
        email = "john@example.com",
        country = "USA",
        city = "NYC",
        avatarUrl = "a",
        photoUrl = "p",
        phone = "123"
    )

    @Test
    fun addBookmark_insertsEntity_andCompletes() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        coEvery { dao.insertBookmarkedUser(any()) } returns Unit
        val ds = BookmarksLocalDataSource(dao)

        ds.addBookmark(sampleUser("x1"))

        coVerify(exactly = 1) { dao.insertBookmarkedUser(withArg { e ->
            assertEquals("x1", e.id)
            assertEquals("John Doe", e.fullName)
        }) }
    }

    @Test(expected = AppError.Storage::class)
    fun addBookmark_mapsExceptionsToStorageError() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        coEvery { dao.insertBookmarkedUser(any()) } throws IllegalStateException("db")
        val ds = BookmarksLocalDataSource(dao)

        ds.addBookmark(sampleUser())
    }

    @Test
    fun removeBookmarkById_deletes_andCompletes() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        coEvery { dao.deleteBookmarkById(any()) } returns Unit
        val ds = BookmarksLocalDataSource(dao)

        ds.removeBookmarkById("u2")
        coVerify(exactly = 1) { dao.deleteBookmarkById("u2") }
    }

    @Test(expected = AppError.Storage::class)
    fun removeBookmarkById_mapsExceptionsToStorageError() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        coEvery { dao.deleteBookmarkById(any()) } throws RuntimeException("db")
        val ds = BookmarksLocalDataSource(dao)

        ds.removeBookmarkById("u2")
    }

    @Test
    fun bookmarkedUserIds_mapsToSet_andEmits() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        every { dao.getBookmarkedIds() } returns flowOf(listOf("a", "b", "a"))
        val ds = BookmarksLocalDataSource(dao)

        val set = ds.bookmarkedUserIds().first()
        assertEquals(setOf("a", "b"), set)
    }

    @Test
    fun bookmarkedUsers_mapsEntitiesToDomain() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        every { dao.getBookmarkedUsers() } returns flowOf(listOf(sampleEntity("e1"), sampleEntity("e2")))
        val ds = BookmarksLocalDataSource(dao)

        val list = ds.bookmarkedUsers().first()
        assertEquals(2, list.size)
        assertEquals("e1", list[0].id)
        assertEquals("e2", list[1].id)
        assertEquals("John Doe", list[0].fullName)
    }

    @Test
    fun flows_mapExceptionsToStorageError() = runTest {
        val dao = mockk<BookmarkedUserDao>()
        every { dao.getBookmarkedIds() } returns flow { throw IllegalArgumentException("x") }
        every { dao.getBookmarkedUsers() } returns flow { throw IllegalStateException("y") }
        val ds = BookmarksLocalDataSource(dao)

        var idsErrorThrown = false
        try {
            ds.bookmarkedUserIds().first()
        } catch (e: Throwable) {
            assertTrue(e is AppError.Storage)
            idsErrorThrown = true
        }
        assertTrue(idsErrorThrown)

        var usersErrorThrown = false
        try {
            ds.bookmarkedUsers().first()
        } catch (e: Throwable) {
            assertTrue(e is AppError.Storage)
            usersErrorThrown = true
        }
        assertTrue(usersErrorThrown)
    }
}
