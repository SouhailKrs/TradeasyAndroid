package com.tradeasy.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.User
import com.tradeasy.domain.usecase.UserDetailsUseCase
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
class UserDetailsViewModel @Inject constructor(private val userDetailsUseCase: UserDetailsUseCase) :
    ViewModel() {
    private val state = MutableStateFlow<ActivityState>(ActivityState.Init)
    val mState: StateFlow<ActivityState> get() = state


    private fun setLoading() {
        state.value = ActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = ActivityState.IsLoading(false)
    }

   /* private fun showToast(message: String) {
        state.value = ActivityState.ShowToast(message)
    }*/


    fun getData() {
        viewModelScope.launch {
            userDetailsUseCase.execute().onStart {
                setLoading()
                println("start")
            }.catch { exception ->
                hideLoading()
                println("exception: " + exception.message)
                //showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = ActivityState.ErrorGettingUserData(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> {
                        state.value = ActivityState.SuccessGettingUserData(baseResult.data)
                    }

                }
            }
        }
    }


}

sealed class ActivityState {
    object Init : ActivityState()
    data class IsLoading(val isLoading: Boolean) : ActivityState()
    data class ShowToast(val message: String) : ActivityState()
    data class SuccessGettingUserData(val user: User) : ActivityState()
    data class ErrorGettingUserData(val rawResponse: WrappedResponse<User>) : ActivityState()
}