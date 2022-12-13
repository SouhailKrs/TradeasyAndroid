package com.tradeasy.ui.login.forgotPassword.resetPass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.ResetPasswordReq
import com.tradeasy.domain.model.User
import com.tradeasy.domain.usecase.ResetPasswordUseCase
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
class ResetPasswordViewModel @Inject constructor(
    private val getResetPasswordUseCase: ResetPasswordUseCase
    ) : ViewModel() {
    private val state = MutableStateFlow<ResetPasswordActivityState>(ResetPasswordActivityState.Init)
    val mState: StateFlow<ResetPasswordActivityState> get() = state


    private fun setLoading() {
        state.value = ResetPasswordActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = ResetPasswordActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = ResetPasswordActivityState.ShowToast(message)
    }


    fun updatePassword(req:ResetPasswordReq) {

        viewModelScope.launch {
            getResetPasswordUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = ResetPasswordActivityState.ErrorUpdate(baseResult.rawResponse)
                        println(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> state.value =
                        ResetPasswordActivityState.SuccessUpdate(baseResult.data)
                }
            }
        }
    }
}

sealed class ResetPasswordActivityState {
    object Init : ResetPasswordActivityState()
    data class IsLoading(val isLoading: Boolean) : ResetPasswordActivityState()
    data class ShowToast(val message: String) : ResetPasswordActivityState()
    data class SuccessUpdate(val user: User) : ResetPasswordActivityState()
    data class ErrorUpdate(val rawResponse: WrappedResponse<User>) : ResetPasswordActivityState()
}