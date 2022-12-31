package com.tradeasy.ui.selling.smsToVerify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.usecase.SmsToVerifyUseCase
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
class SmsToVerifyViewModel @Inject constructor(
    private val smsToVerifyUseCase: SmsToVerifyUseCase
) : ViewModel() {
    private val state = MutableStateFlow<SmsToVerifyState>(SmsToVerifyState.Init)
    val mState: StateFlow<SmsToVerifyState> get() = state


    private fun setLoading() {
        state.value = SmsToVerifyState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = SmsToVerifyState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = SmsToVerifyState.ShowToast(message)
    }


    fun sendSms() {

        viewModelScope.launch {
            smsToVerifyUseCase.execute( ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value =
                            SmsToVerifyState.ErrorSent(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        SmsToVerifyState.SuccessSent("success")
                }
            }
        }
    }
}

sealed class SmsToVerifyState {
    object Init : SmsToVerifyState()
    data class IsLoading(val isLoading: Boolean) : SmsToVerifyState()
    data class ShowToast(val message: String) : SmsToVerifyState()
    data class SuccessSent(val message: String) : SmsToVerifyState()
    data class ErrorSent(val rawResponse: WrappedResponse<String>) : SmsToVerifyState()
}