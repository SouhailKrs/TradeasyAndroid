package com.tradeasy.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.domain.user.usecase.VerifyUsernameUseCase
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
class VerifyUsernameViewModel @Inject constructor(
    private val verifyUsernameUseCase: VerifyUsernameUseCase
) : ViewModel() {
    private val state = MutableStateFlow<VerifyUsernameFragmentState>(VerifyUsernameFragmentState.Init)
    val mState: StateFlow<VerifyUsernameFragmentState> get() = state


    private fun setLoading() {
        state.value = VerifyUsernameFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = VerifyUsernameFragmentState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = VerifyUsernameFragmentState.ShowToast(message)
    }


    fun verifyUsername(req: UpdateUsernameReq) {

        viewModelScope.launch {
            verifyUsernameUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = VerifyUsernameFragmentState.UsernameAvailable(baseResult.rawResponse)
                        println("12121" + baseResult.rawResponse)
                    }
                    is BaseResult.Success -> state.value =
                        VerifyUsernameFragmentState.UsernameExists(baseResult.data)



                }
            }
        }
    }
}

sealed class VerifyUsernameFragmentState {
    object Init : VerifyUsernameFragmentState()
    data class IsLoading(val isLoading: Boolean) : VerifyUsernameFragmentState()
    data class ShowToast(val message: String) : VerifyUsernameFragmentState()
    data class UsernameExists(val message: String) : VerifyUsernameFragmentState()
    data class UsernameAvailable(val rawResponse: WrappedResponse<String>) : VerifyUsernameFragmentState()
}