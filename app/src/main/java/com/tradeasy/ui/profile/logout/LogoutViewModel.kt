package com.tradeasy.ui.profile.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.usecase.LogoutUseCase
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
class LogoutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val state = MutableStateFlow<LogoutState >(LogoutState .Init)
    val mState: StateFlow<LogoutState > get() = state


    private fun setLoading() {
        state.value = LogoutState .IsLoading(true)
    }

    private fun hideLoading() {
        state.value = LogoutState .IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = LogoutState .ShowToast(message)
    }


    fun logout() {

        viewModelScope.launch {
            logoutUseCase.execute( ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value =
                            LogoutState .ErrorLogout(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        LogoutState .SuccessLogout("success")
                }
            }
        }
    }
}

sealed class LogoutState {
    object Init : LogoutState ()
    data class IsLoading(val isLoading: Boolean) : LogoutState ()
    data class ShowToast(val message: String) : LogoutState ()
    data class SuccessLogout(val message: String) : LogoutState ()
    data class ErrorLogout(val rawResponse: WrappedResponse<String>) : LogoutState ()
}