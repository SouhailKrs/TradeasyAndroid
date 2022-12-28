package com.tradeasy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDataViewModel : ViewModel(){

    private val _prodName = MutableLiveData<String>("")
    val prodName: LiveData<String> = _prodName
    fun setProdName(prodName: String) {
        _prodName.value = prodName
    }
}