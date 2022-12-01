package com.tradeasy.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.SearchReq
import com.tradeasy.domain.usecase.SearchProductByNameUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchProductByNameUseCase: SearchProductByNameUseCase) : ViewModel(){
    private val state = MutableStateFlow<SearchFragmentSate>(SearchFragmentSate.Init)
    val mState: StateFlow<SearchFragmentSate> get() = state

    private val products = MutableStateFlow<List<Product>>(mutableListOf())
    val mProducts: StateFlow<List<Product>> get() = products

    init {
        fetchSearchedProducts(name=SearchReq(""))
    }


    private fun setLoading(){
        state.value = SearchFragmentSate.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = SearchFragmentSate.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = SearchFragmentSate.ShowToast(message)
    }

    fun fetchSearchedProducts(name: SearchReq){
        viewModelScope.launch {
            searchProductByNameUseCase.invoke( name)
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

sealed class SearchFragmentSate {
    object Init : SearchFragmentSate()
    data class IsLoading(val isLoading: Boolean) : SearchFragmentSate()
    data class ShowToast(val message : String) : SearchFragmentSate()
}