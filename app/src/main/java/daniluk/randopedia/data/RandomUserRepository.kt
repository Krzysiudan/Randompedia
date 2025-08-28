

package daniluk.randopedia.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import daniluk.randopedia.data.local.database.RandomUser
import daniluk.randopedia.data.local.database.RandomUserDao
import javax.inject.Inject

interface RandomUserRepository {
    val randomUsers: Flow<List<String>>

    suspend fun add(name: String)
}

class DefaultRandomUserRepository @Inject constructor(
    private val randomUserDao: RandomUserDao
) : RandomUserRepository {

    override val randomUsers: Flow<List<String>> =
        randomUserDao.getRandomUsers().map { items -> items.map { it.name } }

    override suspend fun add(name: String) {
        randomUserDao.insertRandomUser(RandomUser(name = name))
    }
}
