package com.tradeasy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.usecase.GetProductsForBid
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val getProductsForBid: GetProductsForBid) : ViewModel(){
    private val state = MutableStateFlow<HomeFragmentState>(HomeFragmentState.Init)
    val mState: StateFlow<HomeFragmentState> get() = state

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchProductsForBid()
    }


    private fun setLoading(){
        state.value = HomeFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = HomeFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = HomeFragmentState.ShowToast(message)
    }

     fun fetchProductsForBid(){
        viewModelScope.launch {
            getProductsForBid.invoke()
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
                            products.value = result.data
                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message)
                        }
                    }
                }
        }
    }

}

sealed class HomeFragmentState {
    object Init : HomeFragmentState()
    data class IsLoading(val isLoading: Boolean) : HomeFragmentState()
    data class ShowToast(val message : String) : HomeFragmentState()
}