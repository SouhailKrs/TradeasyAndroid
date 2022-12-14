package com.tradeasy.ui.home.buyNow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.BuyNowReq
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.product.usecase.BuyNowUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyNowViewModel @Inject constructor(
    private val buyNowUseCase: BuyNowUseCase
) : ViewModel() {

    private val state = MutableStateFlow<BuyNowActivityState>(BuyNowActivityState.Init)
    val mState: StateFlow<BuyNowActivityState> get() = state

    private fun setLoading() {
        state.value = BuyNowActivityState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = BuyNowActivityState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = BuyNowActivityState.ShowToast(message)
    }

    fun buyNow(req: BuyNowReq) {
        viewModelScope.launch {
        buyNowUseCase.invoke(req ).onStart {
            setLoading()
        }.catch { exception ->
            hideLoading()
            showToast(exception.printStackTrace().toString())
        }.collect { baseResult ->
            hideLoading()
            when (baseResult) {
                is BaseResult.Error -> {
                    state.value = BuyNowActivityState.ErrorBuying(baseResult.rawResponse)
                    println("12121" + baseResult.rawResponse)
                }
                is BaseResult.Success -> state.value =
                    BuyNowActivityState.SuccessBuying



            }
        }
    }
}
}
sealed class BuyNowActivityState {

    object Init: BuyNowActivityState()
    object SuccessBuying: BuyNowActivityState()
    data class IsLoading(val isLoading: Boolean) : BuyNowActivityState()
    data class ShowToast(val message: String) : BuyNowActivityState()
    data class ErrorBuying(val rawResponse: WrappedResponse<Product>) : BuyNowActivityState()

}