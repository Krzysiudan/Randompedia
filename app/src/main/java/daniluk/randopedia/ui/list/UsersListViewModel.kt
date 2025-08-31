package daniluk.randopedia.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import daniluk.randopedia.domain.AppError
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import daniluk.randopedia.domain.userMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val randomUserRepository: RandomUserRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    private val basePaging: Flow<PagingData<User>> =
        randomUserRepository.userPagingFlow().cachedIn(viewModelScope)

    private val bookmarkedIds: StateFlow<Set<String>> =
        randomUserRepository
            .bookmarkedUserIds()
            .catch { e ->
                val msg = (e as? AppError)?.userMessage() ?: "Failed to load bookmarks"
                _events.emit(UiEvent.ShowMessage(msg))
                emit(emptySet())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet()
            )

    val bookmarkedUsers: StateFlow<List<User>> =
        randomUserRepository
            .bookmarkedUsers()
            .catch { e ->
                val msg = (e as? AppError)?.userMessage() ?: "Failed to load bookmarks"
                _events.emit(UiEvent.ShowMessage(msg))
                emit(emptyList())
            }
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
            runCatching {
                if (isBookmarked) {
                    randomUserRepository.removeBookmarkById(user.id)
                } else {
                    randomUserRepository.addBookmark(user)
                }
            }.onFailure { e ->
                val msg = (e as? AppError)?.userMessage() ?: "Failed to update bookmark"
                _events.emit(UiEvent.ShowMessage(msg))
            }
        }
    }

    sealed class UiEvent {
        data class ShowMessage(val message: String, val actionLabel: String? = null) : UiEvent()
    }
}
