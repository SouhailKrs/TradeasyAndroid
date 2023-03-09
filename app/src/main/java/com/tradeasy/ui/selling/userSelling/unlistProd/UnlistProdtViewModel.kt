package com.tradeasy.ui.selling.userSelling.unlistProd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.product.usecase.UnlistProdUseCase
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
class UnlistProdtViewModel @Inject constructor(
    private val unlistProdUseCase: UnlistProdUseCase
) : ViewModel() {
    private val state = MutableStateFlow<UnlistProdState>(UnlistProdState.Init)
    val mState: StateFlow<UnlistProdState> get() = state


    private fun setLoading() {
        state.value = UnlistProdState.IsLoading(true)

    }

    private fun hideLoading() {
        state.value = UnlistProdState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = UnlistProdState.ShowToast(message)
    }


    fun unlistProd(req: ProdIdReq) {

        viewModelScope.launch {
            unlistProdUseCase.execute(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value =
                            UnlistProdState.ErrorUnlist(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        UnlistProdState.SuccessUnlist("success")
                }
            }
        }
    }
}

sealed class UnlistProdState {
    object Init : UnlistProdState()
    data class IsLoading(val isLoading: Boolean) : UnlistProdState()
    data class ShowToast(val message: String) : UnlistProdState()
    data class SuccessUnlist(val message: String) : UnlistProdState()
    data class ErrorUnlist(val rawResponse: WrappedResponse<String>) : UnlistProdState()
}