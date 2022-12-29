package com.tradeasy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tradeasy.domain.product.entity.Product

class RecentlyViewedDataViewModel : ViewModel() {

    private val _prodList = MutableLiveData<List<Product>>(mutableListOf())
    val prodList: LiveData<List<Product>> = _prodList

    fun setProdList(List: List<Product>) {
        _prodList.value = prodList.value?.plus(List)
    }




}




