package com.tradeasy.ui.profile.deleteAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.usecase.DeleteAccountUseCase
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
class DeleteAccountViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {
    private val state = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Init)
    val mState: StateFlow<DeleteAccountState> get() = state


    private fun setLoading() {
        state.value = DeleteAccountState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = DeleteAccountState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = DeleteAccountState.ShowToast(message)
    }


    fun deleteAccount() {

        viewModelScope.launch {
            deleteAccountUseCase.execute( ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value =
                            DeleteAccountState.ErrorDelete(baseResult.rawResponse)
                        println(baseResult.rawResponse)
                    }
                    is BaseResult.Success -> state.value =
                        DeleteAccountState.SuccessDelete("success")
                }
            }
        }
    }
}

sealed class DeleteAccountState {
    object Init : DeleteAccountState()
    data class IsLoading(val isLoading: Boolean) : DeleteAccountState()
    data class ShowToast(val message: String) : DeleteAccountState()
    data class SuccessDelete(val message: String) : DeleteAccountState()
    data class ErrorDelete(val rawResponse: WrappedResponse<String>) : DeleteAccountState()
}