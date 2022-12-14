package com.tradeasy.ui.selling.bid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.domain.product.usecase.PlaceBidUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceBidViewModel @Inject constructor(
    private val placeBidUseCase: PlaceBidUseCase
)  : ViewModel() {
    private val state = MutableStateFlow<PlaceBidFragmentState>(PlaceBidFragmentState.Init)
    val mState: StateFlow<PlaceBidFragmentState> get() = state

    private fun setLoading(){
        state.value = PlaceBidFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = PlaceBidFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = PlaceBidFragmentState.ShowToast(message)
    }

    private fun successCreate(){
        state.value = PlaceBidFragmentState.SuccessCreate
    }

    fun placeBid(bid: Bid){
        viewModelScope.launch {
            placeBidUseCase.invoke(bid)
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

sealed class PlaceBidFragmentState {
    object Init: PlaceBidFragmentState()
    object SuccessCreate : PlaceBidFragmentState()
    data class IsLoading(val isLoading: Boolean) : PlaceBidFragmentState()
    data class ShowToast(val message: String) : PlaceBidFragmentState()
}