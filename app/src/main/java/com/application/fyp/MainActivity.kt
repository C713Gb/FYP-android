package com.application.fyp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.application.fyp.Constants.showToast
import com.application.fyp.network.RetrofitApi
import com.application.fyp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: RetrofitApi

    private lateinit var walletAddressText: TextView
    private lateinit var walletBalanceText: TextView
    private lateinit var transactionEditText: EditText
    private lateinit var toEditText: EditText
    private lateinit var sendCard: CardView
    private lateinit var txCard: CardView
    private lateinit var pd: ProgressDialog

    private var eth: String = ""
    private var address: String = ""
    private var hash: String = ""

    private var txUrl = "https://goerli.etherscan.io/tx/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        walletAddressText = findViewById(R.id.addressText)
        walletBalanceText = findViewById(R.id.balanceText)
        transactionEditText = findViewById(R.id.transactionEditText)
        toEditText = findViewById(R.id.toEditText)
        sendCard = findViewById(R.id.sendCard)
        txCard = findViewById(R.id.txCard)

        pd = ProgressDialog(this@MainActivity)
        pd.setMessage("Processing Transaction")
        pd.setCancelable(false)
        pd.setCanceledOnTouchOutside(false)

        retrofit = RetrofitClient.create()

        transactionEditText.addTextChangedListener(object : TextWatcher{
             override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

             }

             override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                 eth = if (p0 != null){
                     val s = p0.toString().trim()
                     s.ifEmpty {
                         ""
                     }
                 } else {
                     ""
                 }
             }

             override fun afterTextChanged(p0: Editable?) {
             }

         })

        toEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                address = if (p0 != null){
                    val s = p0.toString().trim()
                    s.ifEmpty {
                        ""
                    }
                } else {
                    ""
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        fetchNetworkBalance()

        sendCard.setOnClickListener {
            sendMoney()
        }

        txCard.visibility = View.GONE

        txCard.setOnClickListener {
            val txIntent = Intent(Intent.ACTION_VIEW)
            Log.d("TAG", "onCreate: ${txUrl}${hash}")

            txIntent.data = Uri.parse(txUrl+hash)
            startActivity(txIntent)
        }

    }

    private fun sendMoney() {
        if (allGood()){
            disableCard()
            sendMoneyApi()
        }
    }

    private fun sendMoneyApi() {
        pd.show()

        try {
            val call =
                retrofit.sendMoney(network = "goerli", SendMoneyBody(to = address, amount = eth))
            call.enqueue(object : Callback<SendMoneyResponse> {
                override fun onResponse(
                    call: Call<SendMoneyResponse>,
                    response: Response<SendMoneyResponse>
                ) {
                    pd.dismiss()

                    if (!response.isSuccessful) {
                        showToast(this@MainActivity, "Something went wrong!")
                        return
                    }

                    val sendMoneyResponse = response.body()
                    if (sendMoneyResponse != null) {
                        sendMoneyResponse.message?.let { showToast(this@MainActivity, it) }

                        hash = sendMoneyResponse.transactionHash.toString()
                        Log.d("TAG", "onResponse: $hash")

                        txCard.visibility = View.VISIBLE

                        fetchNetworkBalance()
                    }

                    enableCard()
                }

                override fun onFailure(call: Call<SendMoneyResponse>, t: Throwable) {
                    pd.dismiss()
                    t.message?.let { showToast(this@MainActivity, it) }
                    enableCard()

                    txCard.visibility = View.GONE
                }

            })
        }
        catch (e: Exception){
            pd.dismiss()
            e.message?.let { showToast(this@MainActivity, it) }
            enableCard()
            txCard.visibility = View.GONE
        }
    }

    private fun allGood(): Boolean {
        if (eth.isNullOrEmpty()){
            showToast(this, "Enter ETH Value")
            return false
        }

        if (address.isNullOrEmpty()){
            showToast(this, "Enter Address Value")
            return false
        }

        return true
    }

    private fun disableCard(){
        sendCard.isClickable = false
    }

    private fun enableCard(){
        sendCard.isClickable = true
    }

    private fun fetchNetworkBalance() {

        val call = retrofit.fetchNetworkBalance(network = "goerli")
        call.enqueue(object : Callback<WalletResponse>{
            override fun onResponse(
                call: Call<WalletResponse>,
                response: Response<WalletResponse>
            ) {
                if (!response.isSuccessful){
                    showToast(this@MainActivity, "Something went wrong!")
                    return
                }

                val walletResponse = response.body()
                if (walletResponse != null){
                    walletBalanceText.text = "Balance: ${walletResponse.balance} ETH"
                    walletAddressText.text = "Address: ${walletResponse.address}"
                }
            }

            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                t.message?.let { showToast(this@MainActivity, it) }
            }

        })
    }


}