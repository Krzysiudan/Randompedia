package daniluk.randopedia.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val randomUserRepository: RandomUserRepository
) : ViewModel() {

    // 1) Build the base paging flow ONCE and cache it in the VM
    private val basePaging: Flow<PagingData<User>> =
        randomUserRepository.userPagingFlow().cachedIn(viewModelScope)

    // Flow of bookmarked ids to be overlaid on UI
    val bookmarkedIds: StateFlow<Set<String>> =
        randomUserRepository
            .bookmarkedUserIds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    // Full list of bookmarked users to drive the Bookmarks tab
    val bookmarkedUsers: StateFlow<List<User>> =
        randomUserRepository
            .bookmarkedUsers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // UI Paging flow with bookmark state embedded
    val uiPagingFlow: Flow<PagingData<UserUiModel>> = basePaging
        .combine(bookmarkedIds) { paging, ids ->
            paging.map { user -> UserUiModel(user = user, isBookmarked = ids.contains(user.id)) }
        }

    fun onBookmarkClicked(user: User) {
        val isBookmarked = bookmarkedIds.value.contains(user.id)
        viewModelScope.launch {
            try {
                if (isBookmarked) {
                    randomUserRepository.removeBookmarkById(user.id)
                } else {
                    randomUserRepository.addBookmark(user)
                }
            } catch (_: Throwable) {
                // TODO: surface error
            }
        }
    }

}

sealed interface RandomUserUiState {
    object Loading : RandomUserUiState
    data class Error(val throwable: Throwable) : RandomUserUiState
    data class Success(val data: List<String>) : RandomUserUiState
}
