package com.tradeasy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestViewModel : ViewModel() {

    private val _prodName = MutableLiveData<String>("")
    val prodName: LiveData<String> = _prodName
    private val _prodDesc = MutableLiveData<String>("")
    val prodDesc: LiveData<String> = _prodDesc
    private val _prodCat = MutableLiveData<String>("")
    val prodCat: LiveData<String> = _prodCat
    private val _prodPrice = MutableLiveData<Float>(0f)
    val prodPrice: LiveData<Float> = _prodPrice
    private val _bidEndDate = MutableLiveData<Number>(0)
    val bidEndDate: LiveData<Number> = _bidEndDate
    private val _prodQuantity = MutableLiveData<Int>(0)
    val prodQuantity: LiveData<Int> = _prodQuantity
    private val _addedDate = MutableLiveData<Number>(0)
    val addedDate: LiveData<Number> = _addedDate
    private val _forBid = MutableLiveData<Boolean>(false)
    val forBid: LiveData<Boolean> = _forBid
    private val _prodId = MutableLiveData<String>("")
    val prodId: LiveData<String> = _prodId
    private val _ownerUsername = MutableLiveData<String>("")
    val ownerUsername: LiveData<String> = _ownerUsername
    private val _ownerPhoneNumber = MutableLiveData<String>("")
    val ownerPhoneNumber: LiveData<String> = _ownerPhoneNumber
    private val _ownerProfilePicture = MutableLiveData<String>("")
    val ownerProfilePicture: LiveData<String> = _ownerProfilePicture
    private val _prodImages = MutableLiveData<Array<String>>(arrayOf())
    val prodImages: LiveData<Array<String>> = _prodImages
    private val _selling = MutableLiveData<Boolean>(false)
    val selling: LiveData<Boolean> = _selling

    fun setProdName(name: String) {
        _prodName.value = name
    }

    fun setProdDesc(desc: String) {
        _prodDesc.value = desc
    }

    fun setProdCat(cat: String) {
        _prodCat.value = cat
    }

    fun setProdPrice(price: Float) {
        _prodPrice.value = price
    }

    fun setBidEndDate(date: Number) {
        _bidEndDate.value = date
    }

    fun setProdQuantity(quantity: Int) {
        _prodQuantity.value = quantity
    }

    fun setAddedDate(date: Number) {
        _addedDate.value = date
    }

    fun setForBid(forBid: Boolean) {
        _forBid.value = forBid

    }

    fun setProdId(id: String) {
        _prodId.value = id
    }

    fun setOwnerUsername(username: String) {
        _ownerUsername.value = username
    }

    fun setOwnerPhoneNumber(phoneNumber: String) {
        _ownerPhoneNumber.value = phoneNumber
    }

    fun setOwnerProfilePicture(profilePicture: String) {
        _ownerProfilePicture.value = profilePicture
    }

    fun setProdImages(images: Array<String>) {
        _prodImages.value = images
    }

    fun setProdSelling(selling: Boolean) {
        _selling.value = selling
    }
}




