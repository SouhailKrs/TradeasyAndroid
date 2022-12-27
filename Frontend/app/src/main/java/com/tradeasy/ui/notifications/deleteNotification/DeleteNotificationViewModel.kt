package com.tradeasy.ui.notifications.deleteNotification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.data.user.remote.dto.DeleteNotificationReq
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.usecase.DeleteNotificationUseCase
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DeleteNotificationViewModel @Inject constructor(private val deleteNotificationUseCase: DeleteNotificationUseCase) :
    ViewModel() {
    private val state =
        MutableStateFlow<DeleteNotificationFragmentState>(DeleteNotificationFragmentState.Init)
    val mState: StateFlow<DeleteNotificationFragmentState> get() = state
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val notifications = MutableStateFlow<List<Notification>>(mutableListOf())
    val mNotifications: StateFlow<List<Notification>> get() = notifications


//    init {
//        fetchNotifications()
//    }

    private fun setLoading() {
        state.value = DeleteNotificationFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = DeleteNotificationFragmentState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = DeleteNotificationFragmentState.ShowToast(message)
        println("error is $message")
    }

    fun deleteNotification(req:DeleteNotificationReq) {
        viewModelScope.launch {
            deleteNotificationUseCase.execute(req).onStart {
                    setLoading()
                }.catch { exception ->

                    hideLoading()
                    showToast(exception.message.toString())
                }.collect { result ->

                    hideLoading()
                    when (result) {
                        is BaseResult.Success -> {


                            notifications.value = result.data

                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message)
                            println("error is ${result.rawResponse.message}")
                        }
                    }
                }
        }
    }

}

sealed class DeleteNotificationFragmentState {
    object Init : DeleteNotificationFragmentState()
    data class SuccessGettingNotification(val notification: Notification) :
        DeleteNotificationFragmentState()
    data class IsLoading(val isLoading: Boolean) : DeleteNotificationFragmentState()
    data class ShowToast(val message: String) : DeleteNotificationFragmentState()
}