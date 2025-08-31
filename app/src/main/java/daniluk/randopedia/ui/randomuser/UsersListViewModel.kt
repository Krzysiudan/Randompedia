

package daniluk.randopedia.ui.randomuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import daniluk.randopedia.data.RandomUserRepository
import daniluk.randopedia.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val randomUserRepository: RandomUserRepository
) : ViewModel() {

    // 1) Build the base paging flow ONCE and cache it in the VM
    private val basePaging: Flow<PagingData<User>> =
        randomUserRepository.pager().flow.cachedIn(viewModelScope)

    // Flow of bookmarked ids to be overlaid on UI
    val bookmarkedIds: StateFlow<Set<String>> =
        randomUserRepository
            .bookmarkedIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    // UI Paging flow with bookmark state embedded
    val uiPagingFlow: Flow<PagingData<UserUiModel>> =basePaging
            .combine(bookmarkedIds) { paging, ids ->
                paging.map { user -> UserUiModel(user = user, isBookmarked = ids.contains(user.id)) }
            }

    fun onBookmarkClicked(user: User) = viewModelScope.launch {
        // TODO: Maybe it needs some refactor so that the info about bookmark comes from the ui?
        val isBookmarked = bookmarkedIds.value.contains(user.id)
        runCatching {
            if (isBookmarked) {
                randomUserRepository.removeById(user.id)
            } else {
                randomUserRepository.add(user)
            }
        }.onFailure { /* TODO: surface error */ }
    }

}

sealed interface RandomUserUiState {
    object Loading : RandomUserUiState
    data class Error(val throwable: Throwable) : RandomUserUiState
    data class Success(val data: List<String>) : RandomUserUiState
}
