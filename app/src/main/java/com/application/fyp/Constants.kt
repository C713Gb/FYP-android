package com.application.fyp

import android.content.Context
import android.widget.Toast

object Constants {

    val BASE_URL = " https://a468-152-58-210-86.ngrok-free.app"



    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}