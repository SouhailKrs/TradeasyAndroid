package com.tradeasy.ui.selling.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.usecase.CreateProductUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProductsUseCase: CreateProductUseCase)  : ViewModel() {
    private val state = MutableStateFlow<AddProductFragmentSate>(AddProductFragmentSate.Init)
    val mState: StateFlow<AddProductFragmentSate> get() = state

    private fun setLoading(){
        state.value = AddProductFragmentSate.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = AddProductFragmentSate.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = AddProductFragmentSate.ShowToast(message)
    }

    private fun successCreate(){
        state.value = AddProductFragmentSate.SuccessCreate
    }

    fun addProduct(product: Product){
        viewModelScope.launch {
            addProductsUseCase.invoke(product)
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
                        is BaseResult.Success -> successCreate()
                        is BaseResult.Error -> showToast(result.rawResponse.message)
                    }
                }
        }
    }
}

sealed class AddProductFragmentSate {
    object Init: AddProductFragmentSate()
    object SuccessCreate : AddProductFragmentSate()
    data class IsLoading(val isLoading: Boolean) : AddProductFragmentSate()
    data class ShowToast(val message: String) : AddProductFragmentSate()
}