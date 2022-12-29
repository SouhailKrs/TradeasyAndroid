package com.tradeasy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.GetProductsForBid
import com.tradeasy.ui.RecentlyViewedDataViewModel
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
    private val state = MutableStateFlow<ForBidState>(ForBidState.Init)
    val mState: StateFlow<ForBidState> get() = state
 val _isLoading = MutableStateFlow(true)
private var recentlyViewedDataViewModel: RecentlyViewedDataViewModel? = null

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchProductsForBid()

    }


    private fun setLoading(){
        state.value = ForBidState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = ForBidState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = ForBidState.ShowToast(message)
    }

     fun fetchProductsForBid(){
        viewModelScope.launch {
            _isLoading.value = false
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

sealed class ForBidState {
    object Init : ForBidState()
    data class IsLoading(val isLoading: Boolean) : ForBidState()
    data class ShowToast(val message : String) : ForBidState()
}
