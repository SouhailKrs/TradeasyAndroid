package com.tradeasy.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val userRegisterUseCase: RegisterUseCase
) : ViewModel() {
    private val state = MutableStateFlow<UserRegisterActivityState>(UserRegisterActivityState.Init)
    val mState: StateFlow<UserRegisterActivityState> get() = state


    private fun setLoading() {
        state.value = UserRegisterActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = UserRegisterActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = UserRegisterActivityState.ShowToast(message)
    }


    fun userRegister(user: User) {
        viewModelScope.launch {
            userRegisterUseCase.execute(user).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> state.value =
                        UserRegisterActivityState.RegisterError(baseResult.rawResponse)
                    is BaseResult.Success -> state.value =
                        UserRegisterActivityState.RegisterSuccess(baseResult.data)
                    else -> {
                        state.value = UserRegisterActivityState.ShowToast("Something went wrong")
                    }
                }
            }
        }
    }
}

sealed class UserRegisterActivityState {
    object Init : UserRegisterActivityState()
    data class IsLoading(val isLoading: Boolean) : UserRegisterActivityState()
    data class ShowToast(val message: String) : UserRegisterActivityState()
    data class RegisterSuccess(val user: User) : UserRegisterActivityState()
    data class RegisterError(val rawResponse: WrappedResponse<User>) : UserRegisterActivityState()

}