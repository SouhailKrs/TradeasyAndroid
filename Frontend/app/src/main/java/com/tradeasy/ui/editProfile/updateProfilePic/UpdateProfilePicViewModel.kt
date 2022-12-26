package com.tradeasy.ui.editProfile.updateProfilePic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.entity.User
import com.tradeasy.domain.user.usecase.UploadProfilePicUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UpdateProfilePicViewModel @Inject constructor(
    private val uploadProfilePicUseCase: UploadProfilePicUseCase
)  : ViewModel() {
    private val state = MutableStateFlow<UploadProfilePicSate>(UploadProfilePicSate.Init)
    val mState: StateFlow<UploadProfilePicSate> get() = state

    private fun setLoading(){
        state.value = UploadProfilePicSate.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = UploadProfilePicSate.IsLoading(false)

    }

    private fun showToast(message: String){
        state.value = UploadProfilePicSate.ShowToast(message)

    }



    fun uploadProfilePic(image: MultipartBody.Part){
        viewModelScope.launch {
            uploadProfilePicUseCase.invoke(image)
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
                        is BaseResult.Success -> state.value = UploadProfilePicSate.SuccessCreate(result.data)
                        is BaseResult.Error -> showToast(result.rawResponse.message)

                    }
                }
        }
    }
}

sealed class UploadProfilePicSate {
    object Init: UploadProfilePicSate()
    data class SuccessCreate(val user: User) : UploadProfilePicSate()
    data class IsLoading(val isLoading: Boolean) : UploadProfilePicSate()
    data class ShowToast(val message: String) : UploadProfilePicSate()
}