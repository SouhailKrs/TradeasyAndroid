package com.tradeasy.ui.editProfile

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
class EditProfileViewModel @Inject constructor(private val userDetailsUseCase: UserDetailsUseCase) :
    ViewModel() {
    private val state = MutableStateFlow<EditProfileActivityState>(EditProfileActivityState.Init)
    val mState: StateFlow<EditProfileActivityState> get() = state


    private fun setLoading() {
        state.value = EditProfileActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = EditProfileActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = EditProfileActivityState.ShowToast(message)
    }


    fun getData() {
        viewModelScope.launch {
            userDetailsUseCase.execute().onStart {
                setLoading()
                println("start")
            }.catch { exception ->
                hideLoading()
                println("exception: " + exception.message)
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = EditProfileActivityState.ErrorGettingUserData(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> {
                        state.value = EditProfileActivityState.SuccessGettingUserData(baseResult.data)
                    }

                }
            }
        }
    }


}

sealed class EditProfileActivityState {
    object Init : EditProfileActivityState()
    data class IsLoading(val isLoading: Boolean) : EditProfileActivityState()
    data class ShowToast(val message: String) : EditProfileActivityState()
    data class SuccessGettingUserData(val user: User) : EditProfileActivityState()
    data class ErrorGettingUserData(val rawResponse: WrappedResponse<User>) : EditProfileActivityState()
}