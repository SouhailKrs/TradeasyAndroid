package com.tradeasy.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProdImagesDataViewModel : ViewModel() {

    private val _prodImages = MutableLiveData<List<Uri>>(mutableListOf())
    private val prodImages: LiveData<List<Uri>> = _prodImages

    fun setProdImages(List: List<Uri>) {
        _prodImages.value = prodImages.value?.plus(List)
    }




}




