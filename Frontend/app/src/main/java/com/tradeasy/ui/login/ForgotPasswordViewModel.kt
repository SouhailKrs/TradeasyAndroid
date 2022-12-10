package com.tradeasy.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.ForgotPasswordReq
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.domain.usecase.ForgotPasswordUseCase
import com.tradeasy.domain.usecase.UpdatePasswordDetailsUseCase
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
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {
    private val state = MutableStateFlow<ForgotPasswordActivityState>(ForgotPasswordActivityState.Init)
    val mState: StateFlow<ForgotPasswordActivityState> get() = state


    private fun setLoading() {
        state.value = ForgotPasswordActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = ForgotPasswordActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = ForgotPasswordActivityState.ShowToast(message)
    }


    fun sendResetCode(req:ForgotPasswordReq) {

        viewModelScope.launch {
            forgotPasswordUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = ForgotPasswordActivityState.ErrorUpdate(baseResult.rawResponse)
                        println(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> state.value =
                        ForgotPasswordActivityState.SuccessUpdate(baseResult.data)
                }
            }
        }
    }
}

sealed class ForgotPasswordActivityState {
    object Init : ForgotPasswordActivityState()
    data class IsLoading(val isLoading: Boolean) : ForgotPasswordActivityState()
    data class ShowToast(val message: String) : ForgotPasswordActivityState()
    data class SuccessUpdate(val user: User) : ForgotPasswordActivityState()
    data class ErrorUpdate(val rawResponse: WrappedResponse<User>) : ForgotPasswordActivityState()
}