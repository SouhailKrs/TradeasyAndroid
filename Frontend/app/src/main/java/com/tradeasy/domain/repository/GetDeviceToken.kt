package com.tradeasy.domain.repository

import com.tradeasy.utils.UiState

interface GetDeviceToken {
    suspend fun getDeviceToken(result:(UiState<String>)-> Unit)
}