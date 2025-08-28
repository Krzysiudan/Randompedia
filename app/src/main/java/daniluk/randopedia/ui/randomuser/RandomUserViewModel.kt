

package daniluk.randopedia.ui.randomuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import daniluk.randopedia.data.RandomUserRepository
import daniluk.randopedia.ui.randomuser.RandomUserUiState.Error
import daniluk.randopedia.ui.randomuser.RandomUserUiState.Loading
import daniluk.randopedia.ui.randomuser.RandomUserUiState.Success
import javax.inject.Inject

@HiltViewModel
class RandomUserViewModel @Inject constructor(
    private val randomUserRepository: RandomUserRepository
) : ViewModel() {

    val uiState: StateFlow<RandomUserUiState> = randomUserRepository
        .randomUsers.map<List<String>, RandomUserUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addRandomUser(name: String) {
        viewModelScope.launch {
            randomUserRepository.add(name)
        }
    }
}

sealed interface RandomUserUiState {
    object Loading : RandomUserUiState
    data class Error(val throwable: Throwable) : RandomUserUiState
    data class Success(val data: List<String>) : RandomUserUiState
}
