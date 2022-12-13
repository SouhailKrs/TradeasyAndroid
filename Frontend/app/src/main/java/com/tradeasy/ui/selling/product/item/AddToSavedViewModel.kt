package com.tradeasy.ui.selling.product.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.AddToSavedReq
import com.tradeasy.domain.model.User
import com.tradeasy.domain.usecase.AddProductToSavedUseCase
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
    private val addProductToSavedUseCase: AddProductToSavedUseCase)  : ViewModel() {
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

    private fun successCreate(){
        state.value = AddProductToSavedFragmentSate.SuccessSaving
    }

    fun addProductToSaved(req: AddToSavedReq){
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
                    println("zzzzzz ${result}")

                    when(result){
                        is BaseResult.Success -> successCreate()

                        is BaseResult.Error -> showToast(result.rawResponse.message)
                    }
                }
        }
    }
}

sealed class AddProductToSavedFragmentSate {
    object Init: AddProductToSavedFragmentSate()
    object SuccessSaving : AddProductToSavedFragmentSate()
    data class IsLoading(val isLoading: Boolean) : AddProductToSavedFragmentSate()
    data class ShowToast(val message: String) : AddProductToSavedFragmentSate()
    data class ErrorSaving(val rawResponse: WrappedResponse<User>) : AddProductToSavedFragmentSate()
}