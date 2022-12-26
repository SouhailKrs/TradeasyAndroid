package com.tradeasy.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.usecase.GetUserNotificationsUseCase
import com.tradeasy.utils.BaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject



@HiltViewModel
class NotificationsViewModel @Inject constructor(private val  getUserNotificationsUseCase: GetUserNotificationsUseCase) : ViewModel(){
    private val state = MutableStateFlow<NotificationsFragmentState>(NotificationsFragmentState.Init)
    val mState: StateFlow<NotificationsFragmentState> get() = state

    private val notifications = MutableStateFlow<List<Notification>>(mutableListOf())
    val mNotifications: StateFlow<List<Notification>> get() = notifications

    init {
        fetchNotifications()
    }


    private fun setLoading(){
        state.value = NotificationsFragmentState.IsLoading(true)
    }

    private fun hideLoading(){
        state.value = NotificationsFragmentState.IsLoading(false)
    }

    private fun showToast(message: String){
        state.value = NotificationsFragmentState.ShowToast(message)
    }

    fun fetchNotifications(){
        viewModelScope.launch {
            getUserNotificationsUseCase.invoke()
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
                            notifications.value = result.data

                        }
                        is BaseResult.Error -> {
                            showToast(result.rawResponse.message)
                        }
                    }
                }
        }
    }

}

sealed class NotificationsFragmentState {
    object Init : NotificationsFragmentState()
    data class SuccessGettingNotification(val notification: Notification) : NotificationsFragmentState()
    data class IsLoading(val isLoading: Boolean) : NotificationsFragmentState()
    data class ShowToast(val message : String) : NotificationsFragmentState()
}