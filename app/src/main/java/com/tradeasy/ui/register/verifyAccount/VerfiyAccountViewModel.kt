package com.tradeasy.ui.register.verifyAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.VerifyAccountReq
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.VerifyAccountUseCase
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
class VerfiyAccountViewModel @Inject constructor(
    private val verifyAccountUseCase: VerifyAccountUseCase
    ) : ViewModel() {
    private val state = MutableStateFlow<VerifyAccountState>(VerifyAccountState.Init)
    val mState: StateFlow<VerifyAccountState> get() = state


    private fun setLoading() {
        state.value = VerifyAccountState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = VerifyAccountState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = VerifyAccountState.ShowToast(message)
    }


    fun verifyAccount(req: VerifyAccountReq) {

        viewModelScope.launch {
            verifyAccountUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = VerifyAccountState.ErrorVerify(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        VerifyAccountState.SuccessVerify(baseResult.data)
                }
            }
        }
    }
}

sealed class VerifyAccountState {
    object Init : VerifyAccountState()
    data class IsLoading(val isLoading: Boolean) : VerifyAccountState()
    data class ShowToast(val message: String) : VerifyAccountState()
    data class SuccessVerify(val user: User) : VerifyAccountState()
    data class ErrorVerify(val rawResponse: WrappedResponse<User>) : VerifyAccountState()
}