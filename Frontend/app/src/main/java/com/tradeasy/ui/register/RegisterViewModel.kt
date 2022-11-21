package com.tradeasy.ui.register

<<<<<<< HEAD
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.UserRegister
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
import com.tradeasy.domain.usecase.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
<<<<<<< HEAD
    private val getSignUpCase: RegisterUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = mutableStateOf(TradeasyState())
    val state: State<TradeasyState> = _state


     fun register(userRegister: UserRegister) {
        viewModelScope.launch {
            getSignUpCase.execute(userRegister).onEach { result ->
                when (result) {
                    is BaseResult.Success -> {

                        _state.value = TradeasyState( success = true)
                    }
                    is BaseResult.Error -> {

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

>>>>>>> Souhail
}