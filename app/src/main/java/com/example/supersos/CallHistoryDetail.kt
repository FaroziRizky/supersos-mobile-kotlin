package com.example.supersos

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.Models.CancelCallRequest
import com.example.supersos.Models.HistoryItem
import com.example.supersos.Models.Values
import com.example.supersos.Utils.formatDateTime
import com.example.supersos.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallHistoryDetail : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var messageView: TextView
    private lateinit var coordinateView: TextView
    private lateinit var datetimeView: TextView
    private lateinit var statusView: TextView

    private lateinit var instanceDetailsLayout: LinearLayout
    private lateinit var instancesName: TextView
    private lateinit var instancesPhone: TextView
    private lateinit var instancesEmail: TextView
    private lateinit var instancesAddress: TextView

    private lateinit var buttonCancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_history_detail)

        instanceDetailsLayout = findViewById(R.id.instanceDetailsLayout)
        instancesName = findViewById(R.id.instancesName)
        instancesPhone = findViewById(R.id.instancesPhone)
        instancesEmail = findViewById(R.id.instancesEmail)
        instancesAddress = findViewById(R.id.instancesAddress)

        buttonCancel = findViewById(R.id.buttonCancel)

        imageView = findViewById(R.id.iconTypeDetail)
        messageView = findViewById(R.id.messageDetail)
        coordinateView = findViewById(R.id.coordinateDetail)
        datetimeView = findViewById(R.id.datetimeDetail)
        statusView = findViewById(R.id.statusDetail)
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        val idCall = intent.getIntExtra("id_call", -1)
        if (idCall != -1) {
            fetchCallDetail(idCall)
        } else {
            Toast.makeText(this, "Invalid call ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCallDetail(idCall: Int) {
        val token = TokenManager.getToken(this)
        val apiService = token?.let { RetrofitInstance.create(it) }

        if (apiService != null) {
            apiService.getCallDetail("Bearer $token", idCall).enqueue(object :
                Callback<HistoryItem> {
                override fun onResponse(call: Call<HistoryItem>, response: Response<HistoryItem>) {
                    if (response.isSuccessful) {
                        val historyItem = response.body()
                        if (historyItem != null && historyItem.values.isNotEmpty()) {
                            val callDetail = historyItem.values[0]
                            updateUI(callDetail)
                        }
                    } else {
                        Log.e("CallHistoryDetail", "Error: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<HistoryItem>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("CallHistoryDetail", "API call failed")
                }
            })
        }
    }

    private fun updateUI(callDetail: Values) {
        // Set the icon based on callType
        when (callDetail.callType) {
            1 -> imageView.setImageResource(R.drawable.hospital)
            2 -> imageView.setImageResource(R.drawable.police)
            3 -> imageView.setImageResource(R.drawable.firefighter)
        }


        // Set the text values
        messageView.text = callDetail.message
        coordinateView.text = "${callDetail.longitude}, ${callDetail.latitude}"
        datetimeView.text = formatDateTime(callDetail.appliedAt.toString())

        if (callDetail.idInstances == null) {
            // Hide instanceDetailsLayout
            instanceDetailsLayout.visibility = View.GONE
        } else {
            // Populate instance details and show the layout
            instancesName.text = callDetail.instancesName
            instancesPhone.text = callDetail.instancesPhone
            instancesEmail.text = callDetail.instancesEmail
            instancesAddress.text = callDetail.instancesAddress

            instanceDetailsLayout.visibility = View.VISIBLE
        }

        // Set the status text and color
        val statusBackground = statusView.background as GradientDrawable
        when (callDetail.status) {
            1 -> {
                statusBackground.setColor(this.getColor(R.color.red))
                statusView.text = "Dibatalkan"
                buttonCancel.visibility = View.GONE
            }

            0 -> {
                statusBackground.setColor(this.getColor(R.color.gray))
                statusView.text = "Tertunda"
                buttonCancel.visibility = View.VISIBLE
                buttonCancel.setOnClickListener {
                    callDetail.idCall?.let { it1 -> cancelCall(idCall = it1) }
                    callDetail.idCall?.let { it1 ->
                        fetchCallDetail(
                            it1
                        )
                    }
                    Log.d("ErrorCuy", "Canccelled")
                }
            }

            2 -> {
                statusBackground.setColor(this.getColor(R.color.green))
                statusView.text = "Diterima"
                buttonCancel.visibility = View.GONE
            }

            else -> statusBackground.setColor(this.getColor(R.color.gray))
        }
    }
    private fun cancelCall(idCall: Int) {
        val token = TokenManager.getToken(this)
        val apiService = token?.let { RetrofitInstance.create(it) }


        // Membuat objek permintaan HTTP
        val call: Call<Void?>? =
            apiService!!.cancelCall(token, CancelCallRequest(idCall = idCall.toString())) // Menggunakan Retrofit, Anda perlu mengganti ini dengan metode yang sesuai

        // Melakukan panggilan asinkron ke API
        if (call != null) {
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    // Mengambil status respons
                    val statusCode = response.code()
                    // Tampilkan atau tangani status respons sesuai kebutuhan Anda
                    Toast.makeText(applicationContext, "Panggilan Dibatalkan", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    // Tangani kesalahan jika panggilan gagal
                    Toast.makeText(applicationContext, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
