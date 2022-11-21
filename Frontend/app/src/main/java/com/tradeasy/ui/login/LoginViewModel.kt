package com.tradeasy.ui.login

<<<<<<< HEAD
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister
import com.tradeasy.domain.usecase.LoginUseCase
import com.tradeasy.domain.usecase.RegisterUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.TradeasyState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
=======
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.User
import com.tradeasy.domain.usecase.LoginUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
>>>>>>> Souhail
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
<<<<<<< HEAD
    private val getLoginCase: LoginUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = mutableStateOf(TradeasyState())
    val state: State<TradeasyState> = _state


    fun login(userLogin: UserLogin) {
        viewModelScope.launch {
            getLoginCase.execute(userLogin).onEach { result ->
                when (result) {
                    is BaseResult.Success -> {

                        _state.value = TradeasyState( success = true)
                        println("Login Success")
                    }
                    is BaseResult.Error -> {
                        println(" Login Error")
                        _state.value = TradeasyState(
                            error = result.message ?: "An unexpected error occured"

                        )
                    }
                    is BaseResult.Loading -> {

                        _state.value = TradeasyState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)


        }
    }

=======
    private val getLoginCase: LoginUseCase
) : ViewModel() {
    private val state = MutableStateFlow<LoginActivityState>(LoginActivityState.Init)
    val mState: StateFlow<LoginActivityState> get() = state


    private fun setLoading() {
        state.value = LoginActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = LoginActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = LoginActivityState.ShowToast(message)
    }


    fun login(user: User) {
        viewModelScope.launch {
            getLoginCase.execute(user).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = LoginActivityState.ErrorLogin(baseResult.rawResponse)
                        println(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> state.value =
                        LoginActivityState.SuccessLogin(baseResult.data)
                }
            }
        }
    }
}

sealed class LoginActivityState {
    object Init : LoginActivityState()
    data class IsLoading(val isLoading: Boolean) : LoginActivityState()
    data class ShowToast(val message: String) : LoginActivityState()
    data class SuccessLogin(val user: User) : LoginActivityState()
    data class ErrorLogin(val rawResponse: WrappedResponse<User>) : LoginActivityState()
>>>>>>> Souhail
}