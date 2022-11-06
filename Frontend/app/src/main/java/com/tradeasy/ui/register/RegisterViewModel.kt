package com.tradeasy.ui.register

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
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

}