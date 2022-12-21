package com.tradeasy.ui.profile.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.GetSavedProductsUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class SavedProductsViewModel @Inject constructor(private val getSavedProductsUseCase: GetSavedProductsUseCase) : ViewModel(){
    private val state = MutableStateFlow<SavedProductsFragmentState>(SavedProductsFragmentState.Init)
    val mState: StateFlow<SavedProductsFragmentState> get() = state

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchSavedProducts()
    }


    private fun setLoading(){
        state.value = SavedProductsFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = SavedProductsFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = SavedProductsFragmentState.ShowToast(message)
    }

    fun fetchSavedProducts(){
        viewModelScope.launch {
            getSavedProductsUseCase.invoke()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    println("aaaaa ${exception.message}")
                    hideLoading()
                    showToast(exception.message.toString())
                }
                .collect { result ->
                    println("bbbb $result")
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

sealed class SavedProductsFragmentState {
    object Init : SavedProductsFragmentState()
    data class IsLoading(val isLoading: Boolean) : SavedProductsFragmentState()
    data class ShowToast(val message : String) : SavedProductsFragmentState()
}