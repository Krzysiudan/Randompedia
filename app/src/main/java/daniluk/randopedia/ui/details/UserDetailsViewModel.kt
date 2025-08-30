package daniluk.randopedia.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import daniluk.randopedia.data.model.User
import kotlinx.serialization.json.Json

/**
 * Hilt ViewModel for the user details screen.
 * It derives the UI from the navigation argument `user` (JSON-encoded),
 * and manages bookmark toggle locally.
 */
@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val user: User? = savedStateHandle.get<String>("user")
        ?.let { json -> runCatching { Json.decodeFromString(User.serializer(), json) }.getOrNull() }

    private val _ui: MutableStateFlow<UserDetailsUi> = MutableStateFlow(
        if (user != null) {
            UserDetailsUi(
                photoUrl = user.photoUrl,
                name = user.fullName,
                phone = user.email, // Using email as a placeholder for phone
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
                isBookmarked = false
            )
        }
    )
    val ui: StateFlow<UserDetailsUi> = _ui.asStateFlow()

    fun toggleBookmark() {
        _ui.update { current -> current.copy(isBookmarked = !current.isBookmarked) }
    }

    private fun samplePhotoUrl(seed: String): String {
        val idx = (seed.hashCode().let { if (it < 0) -it else it } % 90)
        return "https://randomuser.me/api/portraits/men/$idx.jpg"
    }
}
