package com.tradeasy.ui.userSettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.domain.usecase.UserDetailsUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.TradeasyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
    class UserDetailsViewModel @Inject constructor(private val userDetailsUseCase: UserDetailsUseCase): ViewModel() {
    private val state = MutableStateFlow<ActivityState>(ActivityState.Init)
    val mState: StateFlow<ActivityState> get() = state


    private fun setLoading(){
        state.value = ActivityState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = ActivityState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = ActivityState.ShowToast(message)
    }


    fun getData(){
        viewModelScope.launch {
            userDetailsUseCase.execute()
                .onStart {
                    setLoading()
                    println("starrt")
                }
                .catch { exception ->
                    hideLoading()
                    println("exc: "+exception.message)
                    showToast(exception.printStackTrace().toString())
                }
                .collect { baseResult ->
                    hideLoading()
                    when(baseResult){
                        is BaseResult.Error -> {
                            println("ops")
                            state.value = ActivityState.ErrorLogin(baseResult.message!!)}
                        is BaseResult.Success -> {
                            println("succ")
                            state.value = ActivityState.SuccessLogin(baseResult.U as User)}
                        else -> {
                            println("qqqq")
                            state.value = ActivityState.ErrorLogin("Something went wrong")
                        }
                    }
                }
        }
    }



}

sealed class ActivityState  {
    object Init : ActivityState()
    data class IsLoading(val isLoading: Boolean) : ActivityState()
    data class ShowToast(val message: String) : ActivityState()
    data class SuccessLogin(val user: User) : ActivityState()
    data class ErrorLogin(val rawResponse: String) : ActivityState()
}