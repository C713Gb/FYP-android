package com.application.fyp

data class WalletResponse(
    val balance: String? = null,
    val address: String? = null,
    val message: String? = null,
    val error: String? = null,
)

data class SendMoneyBody(
    val to: String,
    val amount: String,
)

data class SendMoneyResponse(
    val message: String? = null,
    val transactionHash: String? = null,
    val error: String? = null,
)