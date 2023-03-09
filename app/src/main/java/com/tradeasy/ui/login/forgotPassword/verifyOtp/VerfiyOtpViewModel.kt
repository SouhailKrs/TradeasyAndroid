package com.tradeasy.ui.login.forgotPassword.verifyOtp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.VerifyOtpReq
import com.tradeasy.domain.user.usecase.VerifyOtpUseCase
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
class VerfiyOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {
    private val state = MutableStateFlow<VerifyOtpFragmentState>(VerifyOtpFragmentState.Init)
    val mState: StateFlow<VerifyOtpFragmentState> get() = state


    private fun setLoading() {
        state.value = VerifyOtpFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = VerifyOtpFragmentState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = VerifyOtpFragmentState.ShowToast(message)
    }


    fun verifyOtp(req: VerifyOtpReq) {

        viewModelScope.launch {
            verifyOtpUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = VerifyOtpFragmentState.ErrorUpdate(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        VerifyOtpFragmentState.SuccessUpdate(baseResult.data)



                }
            }
        }
    }
}

sealed class VerifyOtpFragmentState {
    object Init : VerifyOtpFragmentState()
    data class IsLoading(val isLoading: Boolean) : VerifyOtpFragmentState()
    data class ShowToast(val message: String) : VerifyOtpFragmentState()
    data class SuccessUpdate(val message: String) : VerifyOtpFragmentState()
    data class ErrorUpdate(val rawResponse: WrappedResponse<String>) : VerifyOtpFragmentState()
}