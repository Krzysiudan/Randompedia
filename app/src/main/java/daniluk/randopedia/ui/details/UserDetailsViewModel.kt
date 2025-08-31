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
import daniluk.randopedia.data.RandomUserRepository
import daniluk.randopedia.data.model.User
import kotlinx.serialization.json.Json

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

    private val user: User? = savedStateHandle.get<String>("user")
        ?.let { json -> runCatching { Json.decodeFromString(User.serializer(), json) }.getOrNull() }

    private val _ui: MutableStateFlow<UserDetailsUi> = MutableStateFlow(
        if (user != null) {
            UserDetailsUi(
                photoUrl = user.photoUrl,
                name = user.fullName,
                phone = user.phone,
                email = user.email,
                address = "${user.city}, ${user.country}",
                isBookmarked = false
            )
        } else {
            // Fallback
            UserDetailsUi(
                photoUrl = samplePhotoUrl("unknown"),
                name = "Unknown user",
                phone = "",
                address = "",
                email = "",
                isBookmarked = false
            )
        }
    )
    val ui: StateFlow<UserDetailsUi> = _ui.asStateFlow()

    init {
        // Observe database bookmark ids and reflect the state in UI
        val u = user
        if (u != null) {
            viewModelScope.launch {
                randomUserRepository.bookmarkedIds().collectLatest { ids ->
                    val bookmarked = ids.contains(u.id)
                    _ui.update { current -> current.copy(isBookmarked = bookmarked) }
                }
            }
        }
    }

    fun toggleBookmark() {
        val u = user ?: return
        val currentlyBookmarked = ui.value.isBookmarked
        viewModelScope.launch {
            try {
                if (currentlyBookmarked) {
                    randomUserRepository.removeById(u.id)
                } else {
                    randomUserRepository.add(u)
                }
            } catch (_: Throwable) {
                // Swallow for now; could expose error state if needed
            }
        }
    }

    private fun samplePhotoUrl(seed: String): String {
        val idx = (seed.hashCode().let { if (it < 0) -it else it } % 90)
        return "https://randomuser.me/api/portraits/men/$idx.jpg"
    }
}
