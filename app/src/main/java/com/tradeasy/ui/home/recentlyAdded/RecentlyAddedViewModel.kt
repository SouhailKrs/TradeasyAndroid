package com.tradeasy.ui.home.recentlyAdded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.GetRecentlyAddedProductsUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class RecentlyAddedViewModel @Inject constructor(private val getRecentlyAddedViewModel: GetRecentlyAddedProductsUseCase) : ViewModel(){
    private val state = MutableStateFlow<RecentlyAddedState>(RecentlyAddedState.Init)
    val mState: StateFlow<RecentlyAddedState> get() = state
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products
//    private val products = MutableStateFlow<List<Product>>(mutableListOf())
//    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchRecentlyAdded()
    }


    private fun setLoading(){
        state.value = RecentlyAddedState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = RecentlyAddedState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = RecentlyAddedState.ShowToast(message)
    }

    fun fetchRecentlyAdded(){
        viewModelScope.launch {
            getRecentlyAddedViewModel.invoke()
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

sealed class RecentlyAddedState {
    object Init : RecentlyAddedState()
    data class IsLoading(val isLoading: Boolean) : RecentlyAddedState()
    data class ShowToast(val message : String) : RecentlyAddedState()
}