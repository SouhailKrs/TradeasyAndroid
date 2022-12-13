package com.tradeasy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.usecase.UserSellingUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSellingViewModel @Inject constructor(private val userSellingUseCase: UserSellingUseCase) : ViewModel(){
    private val state = MutableStateFlow<SellingFragmentState>(SellingFragmentState.Init)
    val mState: StateFlow<SellingFragmentState> get() = state

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchUserSelling()
    }


    private fun setLoading(){
        state.value = SellingFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = SellingFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = SellingFragmentState.ShowToast(message)
    }

         fun fetchUserSelling(){
        viewModelScope.launch {
            userSellingUseCase.invoke()
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

sealed class SellingFragmentState {
    object Init : SellingFragmentState()
    data class IsLoading(val isLoading: Boolean) : SellingFragmentState()
    data class ShowToast(val message : String) : SellingFragmentState()
}