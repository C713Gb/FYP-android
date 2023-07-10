package com.application.fyp.network

import com.application.fyp.SendMoneyBody
import com.application.fyp.SendMoneyResponse
import com.application.fyp.WalletResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitApi {

    @GET("/")
    fun fetchWalletDetails(): Call<WalletResponse>

    @GET("/balance/{network}")
    fun fetchNetworkBalance(@Path(value = "network") network: String): Call<WalletResponse>

    @POST("/balance/{network}")
    fun sendMoney(
        @Path(value = "network") network: String,
        @Body sendMoneyBody: SendMoneyBody
    ): Call<SendMoneyResponse>

}