package com.tradeasy.ui.selling.product.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.product.usecase.AddProductToSavedUseCase
import com.tradeasy.domain.user.entity.User
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
class AddToSavedViewModel @Inject constructor(
    private val addProductToSavedUseCase: AddProductToSavedUseCase
)  : ViewModel() {
    private val state = MutableStateFlow<AddProductToSavedFragmentSate>(
        AddProductToSavedFragmentSate.Init)
    val mState: StateFlow<AddProductToSavedFragmentSate> get() = state

    private fun setLoading(){
        state.value = AddProductToSavedFragmentSate.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = AddProductToSavedFragmentSate.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = AddProductToSavedFragmentSate.ShowToast(message)
    }


    fun addProductToSaved(req: ProdIdReq){
        viewModelScope.launch {
            addProductToSavedUseCase.invoke(req)
                .onStart {
                    setLoading()

                }
                .catch { exception ->
                    hideLoading()

                    showToast(exception.stackTraceToString())
                }
                .collect { result ->
                    hideLoading()


                    when(result){
                        is BaseResult.Success -> state.value =
                            AddProductToSavedFragmentSate.SuccessSaving(result.data)

                        is BaseResult.Error -> showToast(result.rawResponse.message)
                    }
                }
        }
    }
}

sealed class AddProductToSavedFragmentSate {
    object Init: AddProductToSavedFragmentSate()
    data class SuccessSaving (val user: User) : AddProductToSavedFragmentSate()
    data class IsLoading(val isLoading: Boolean) : AddProductToSavedFragmentSate()
    data class ShowToast(val message: String) : AddProductToSavedFragmentSate()
    data class ErrorSaving(val rawResponse: WrappedResponse<User>) : AddProductToSavedFragmentSate()
}