package com.tradeasy.ui.selling.userSelling.editProduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.usecase.EditProdUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val editProductUseCase: EditProdUseCase
)  : ViewModel() {
    private val state = MutableStateFlow<EditProductFragmentSate>(EditProductFragmentSate.Init)
    val mState: StateFlow<EditProductFragmentSate> get() = state

    private fun setLoading(){
        state.value = EditProductFragmentSate.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = EditProductFragmentSate.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = EditProductFragmentSate.ShowToast(message)
    }

    private fun successCreate(){
        state.value = EditProductFragmentSate.SuccessCreate
    }

    fun editProduct(
        category: MultipartBody.Part,
        name: MultipartBody.Part,
        description: MultipartBody.Part,
        price: MultipartBody.Part,
        image: List<MultipartBody.Part>,
        quantity: MultipartBody.Part,
        for_bid: MultipartBody.Part,
        bid_end_date: MultipartBody.Part,
        prod_id: MultipartBody.Part){
        viewModelScope.launch {
            editProductUseCase.invoke(category,name,description,price, image, quantity, for_bid, bid_end_date, prod_id)
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

sealed class EditProductFragmentSate {
    object Init: EditProductFragmentSate()
    object SuccessCreate : EditProductFragmentSate()
    data class IsLoading(val isLoading: Boolean) : EditProductFragmentSate()
    data class ShowToast(val message: String) : EditProductFragmentSate()
}