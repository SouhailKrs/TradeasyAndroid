package com.tradeasy.ui.selling.userSelling.bidWinner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.GetBidWinnerUseCase
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
class BidWinnerViewModel @Inject constructor(
    private val bidWinnerUseCase: GetBidWinnerUseCase
    ) : ViewModel() {
    private val state = MutableStateFlow<BidWinnerState>(BidWinnerState.Init)
    val mState: StateFlow<BidWinnerState> get() = state


    private fun setLoading() {
        state.value = BidWinnerState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = BidWinnerState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = BidWinnerState.ShowToast(message)
    }


    fun getBidWinner(req: ProdIdReq) {

        viewModelScope.launch {
            bidWinnerUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value = BidWinnerState.ErrorGet(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        BidWinnerState.SuccessGet(baseResult.data)
                }
            }
        }
    }
}

sealed class BidWinnerState {
    object Init : BidWinnerState()
    data class IsLoading(val isLoading: Boolean) : BidWinnerState()
    data class ShowToast(val message: String) : BidWinnerState()
    data class SuccessGet(val user: User) : BidWinnerState()
    data class ErrorGet(val rawResponse: WrappedResponse<User>) : BidWinnerState()
}