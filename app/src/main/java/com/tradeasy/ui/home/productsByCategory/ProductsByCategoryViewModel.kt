package com.tradeasy.ui.home.productsByCategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.GetByCatReq
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.GetProductByCategoryUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsByCategoryViewModel @Inject constructor(private val productByCategoryUseCase: GetProductByCategoryUseCase) : ViewModel(){
    private val state = MutableStateFlow<ProductsByCategoryState>(ProductsByCategoryState.Init)
    val mState: StateFlow<ProductsByCategoryState> get() = state


    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products
//    init {
//       fetchProdByCat( GetByCatReq("cars"))
//    }


    private fun setLoading(){
        state.value = ProductsByCategoryState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = ProductsByCategoryState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = ProductsByCategoryState.ShowToast(message)
    }

    fun fetchProdByCat(category : GetByCatReq){

        viewModelScope.launch {
            productByCategoryUseCase.invoke( category)
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    hideLoading()
                    showToast(exception.message.toString())
                }
                .collect { result ->
                    hideLoading()
                    when(result){
                        is BaseResult.Success -> {

                            _products.value = result.data
                        }
                        is BaseResult.Error -> {

                            showToast(result.rawResponse.message)
                        }
                    }
                }
        }
    }

}

sealed class ProductsByCategoryState {
    object Init : ProductsByCategoryState()

    data class IsLoading(val isLoading: Boolean) : ProductsByCategoryState()
    data class ShowToast(val message : String) : ProductsByCategoryState()
}