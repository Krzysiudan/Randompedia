package daniluk.randopedia.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import daniluk.randopedia.domain.RandomUserRepository
import daniluk.randopedia.domain.model.User
import daniluk.randopedia.ui.list.UsersListViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import daniluk.randopedia.domain.AppError
import daniluk.randopedia.domain.userMessage

/**
 * Hilt ViewModel for the user details screen.
 * It derives the UI from the navigation argument `user` (JSON-encoded),
 * and observes the database for bookmark state.
 */
@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val randomUserRepository: RandomUserRepository,
) : ViewModel() {

    // Fail fast if missing; or make this nullable if details can handle no id.
    private val user: User = checkNotNull(getUserFromNavigation(savedStateHandle)) { "user is required" }

    private val _ui: MutableStateFlow<UserDetailsScreenState> = MutableStateFlow(
            UserDetailsScreenState(
               user = user,
                isBookmarked = false
            )
    )
    val ui: StateFlow<UserDetailsScreenState> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<UsersListViewModel.UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<UsersListViewModel.UiEvent> = _events.asSharedFlow()

    init {
        retrieveBookmarkState()
    }

    private fun retrieveBookmarkState() {
        viewModelScope.launch {
            randomUserRepository.bookmarkedUserIds().collectLatest { ids ->
                val bookmarked = ids.contains(user.id)
                _ui.update { current -> current.copy(isBookmarked = bookmarked) }
            }
        }
    }

    fun toggleBookmark() {
        val currentlyBookmarked = ui.value.isBookmarked
        viewModelScope.launch {
            runCatching {
                if (currentlyBookmarked) {
                    randomUserRepository.removeBookmarkById(user.id)
                } else {
                    randomUserRepository.addBookmark(user)
                }
            }.onFailure { t ->
                val msg = (t as? AppError)?.userMessage() ?: "Failed to update bookmark"
                _events.tryEmit(UsersListViewModel.UiEvent.ShowMessage(message = msg, actionLabel = "UNDO"))
            }
        }
    }

    private fun getUserFromNavigation(savedStateHandle: SavedStateHandle): User? =
        savedStateHandle.get<String>("user")
            ?.let { json ->
                runCatching {
                    Json.decodeFromString(
                        User.serializer(),
                        json
                    )
                }.getOrNull()
            }
}

data class UserDetailsScreenState(
    val user: User,
    val isBookmarked: Boolean = false
)
