package com.tradeasy.ui.editProfile.updatePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.UpdatePasswordRequest
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.UpdatePasswordDetailsUseCase
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
class UpdatePasswordViewModel @Inject constructor(
    private val getUpdatePasswordDetailsUseCase: UpdatePasswordDetailsUseCase
    ) : ViewModel() {
    private val state = MutableStateFlow<UpdatePasswordActivityState>(UpdatePasswordActivityState.Init)
    val mState: StateFlow<UpdatePasswordActivityState> get() = state


    private fun setLoading() {
        state.value = UpdatePasswordActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = UpdatePasswordActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = UpdatePasswordActivityState.ShowToast(message)
    }


    fun updatePassword(req: UpdatePasswordRequest) {

        viewModelScope.launch {
            getUpdatePasswordDetailsUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = UpdatePasswordActivityState.ErrorUpdate(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        UpdatePasswordActivityState.SuccessUpdate(baseResult.data)
                }
            }
        }
    }
}

sealed class UpdatePasswordActivityState {
    object Init : UpdatePasswordActivityState()
    data class IsLoading(val isLoading: Boolean) : UpdatePasswordActivityState()
    data class ShowToast(val message: String) : UpdatePasswordActivityState()
    data class SuccessUpdate(val user: User) : UpdatePasswordActivityState()
    data class ErrorUpdate(val rawResponse: WrappedResponse<User>) : UpdatePasswordActivityState()
}