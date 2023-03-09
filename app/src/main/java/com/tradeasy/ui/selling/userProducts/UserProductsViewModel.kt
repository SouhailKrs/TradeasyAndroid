package com.tradeasy.ui.selling.userProducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.GetUserProductsUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class UserProductsViewModel @Inject constructor(private val getUserProductsUseCase: GetUserProductsUseCase) : ViewModel(){
    private val state = MutableStateFlow<UserProductsState>(UserProductsState.Init)
    val mState: StateFlow<UserProductsState> get() = state

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchUserProducts()
    }


    private fun setLoading(){
        state.value = UserProductsState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = UserProductsState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = UserProductsState.ShowToast(message)
    }

    fun fetchUserProducts(){
        viewModelScope.launch {
            getUserProductsUseCase.invoke()
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

sealed class UserProductsState {
    object Init : UserProductsState()
    data class IsLoading(val isLoading: Boolean) : UserProductsState()
    data class ShowToast(val message : String) : UserProductsState()
}