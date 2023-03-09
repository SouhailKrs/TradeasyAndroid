package com.tradeasy.ui.editProfile.updateUsername

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.UpdateUsernameUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUsernameViewModel @Inject constructor(
    private val getUpdateUsernameUseCase: UpdateUsernameUseCase
    ) : ViewModel() {
    private val state = MutableStateFlow<UpdateUsernameActivityState>(UpdateUsernameActivityState.Init)
    val mState: StateFlow<UpdateUsernameActivityState> get() = state


    private fun setLoading() {
        state.value = UpdateUsernameActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = UpdateUsernameActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = UpdateUsernameActivityState.ShowToast(message)
    }


    fun updateUsername(req: UpdateUsernameReq) {

        viewModelScope.launch {
            getUpdateUsernameUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = UpdateUsernameActivityState.ErrorUpdate(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        UpdateUsernameActivityState.SuccessUpdate(baseResult.data)
                }
            }
        }
    }
}

sealed class UpdateUsernameActivityState {
    object Init : UpdateUsernameActivityState()
    data class IsLoading(val isLoading: Boolean) : UpdateUsernameActivityState()
    data class ShowToast(val message: String) : UpdateUsernameActivityState()
    data class SuccessUpdate(val user: User) : UpdateUsernameActivityState()
    data class ErrorUpdate(val rawResponse: WrappedResponse<User>) : UpdateUsernameActivityState()
}