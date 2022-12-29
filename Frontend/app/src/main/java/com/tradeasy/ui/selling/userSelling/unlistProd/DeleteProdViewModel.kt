package com.tradeasy.ui.selling.userSelling.unlistProd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.product.usecase.DeleteProdUseCase
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
class DeleteProdViewModel @Inject constructor(
    private val deleteProdUseCase: DeleteProdUseCase
) : ViewModel() {
    private val state = MutableStateFlow<DeleteProdState>(DeleteProdState.Init)
    val mState: StateFlow<DeleteProdState> get() = state


    private fun setLoading() {
        state.value = DeleteProdState.IsLoading(true)

    }

    private fun hideLoading() {
        state.value = DeleteProdState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = DeleteProdState.ShowToast(message)
    }


    fun deleteProd(req: ProdIdReq) {

        viewModelScope.launch {
            deleteProdUseCase.invoke(req ).onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.printStackTrace().toString())
            }.collect { baseResult ->
                hideLoading()
                when (baseResult) {
                    is BaseResult.Error -> {
                        state.value =
                            DeleteProdState.ErrorUnlist(baseResult.rawResponse)

                    }
                    is BaseResult.Success -> state.value =
                        DeleteProdState.SuccessUnlist("success")
                }
            }
        }
    }
}

sealed class DeleteProdState {
    object Init : DeleteProdState()
    data class IsLoading(val isLoading: Boolean) : DeleteProdState()
    data class ShowToast(val message: String) : DeleteProdState()
    data class SuccessUnlist(val message: String) : DeleteProdState()
    data class ErrorUnlist(val rawResponse: WrappedResponse<String>) : DeleteProdState()
}