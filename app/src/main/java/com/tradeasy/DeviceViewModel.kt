package com.tradeasy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradeasy.domain.repository.GetDeviceToken
import com.tradeasy.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepo: GetDeviceToken
) : ViewModel() {

    private val _resToken = MutableLiveData<UiState<String>>()
    val resToken: LiveData<UiState<String>>
        get() = _resToken

    fun getToken() {
        viewModelScope.launch {
            _resToken.value = UiState.Loading
            deviceRepo.getDeviceToken {
                _resToken.value = it
            }
        }
    }
}