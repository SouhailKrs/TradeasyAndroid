package com.tradeasy.ui.selling.product.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.category.entity.Category
import com.tradeasy.domain.category.usecase.GetCategoriesUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val categoriesUseCase: GetCategoriesUseCase) : ViewModel(){
    private val state = MutableStateFlow<CategoriesFragmentState>(CategoriesFragmentState.Init)
    val mState: StateFlow<CategoriesFragmentState> get() = state

    private val categories = MutableStateFlow<List<Category>>(mutableListOf())
    val mCategories: StateFlow<List<Category>> get() = categories

    init {
        fetchCategories()
    }


    private fun setLoading(){
        state.value = CategoriesFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = CategoriesFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = CategoriesFragmentState.ShowToast(message)
    }

    fun fetchCategories(){
        viewModelScope.launch {
            categoriesUseCase.invoke()
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
                            categories.value = result.data

                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message)

                        }
                    }
                }
        }
    }

}

sealed class CategoriesFragmentState {
    object Init : CategoriesFragmentState()
    data class IsLoading(val isLoading: Boolean) : CategoriesFragmentState()
    data class ShowToast(val message : String) : CategoriesFragmentState()
}