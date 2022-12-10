package com.tradeasy.domain.model.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.tradeasy.domain.repository.GetDeviceToken
import com.tradeasy.utils.UiState

class GetDeviceTokenImpl:GetDeviceToken {
    override suspend fun getDeviceToken(result: (UiState<String>) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->
            result.invoke(UiState.Success(task))
        }.addOnFailureListener{
            result.invoke(UiState.Failure(it.localizedMessage!!))
        }
    }

}