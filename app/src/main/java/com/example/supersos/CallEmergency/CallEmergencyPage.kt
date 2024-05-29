package com.example.supersos.CallEmergency

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.CurrentLocation.NominatimResponse
import com.example.supersos.CurrentLocation.RetrofitClient
import com.example.supersos.Models.MakeCallRequest
import com.example.supersos.Models.MakeCallResponse
import com.example.supersos.R
import com.example.supersos.api.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallEmergencyPage : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var messageCall: EditText
    private val locationPermissionRequestCode = 1
    private var longitude: Double = 0.00
    private var latitude: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_call_emergency_page)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val emergencyType = intent.getStringExtra("emergency_type")
        val imageView: ImageView = findViewById(R.id.icon)
        val textView: TextView = findViewById(R.id.iconName)
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        messageCall = findViewById(R.id.et_message)

        val btn_sos: Button = findViewById(R.id.btn_sos)
        btnBack.setOnClickListener {
            onBackPressed() // Kembali ke halaman sebelumnya
        }

        when (emergencyType) {
            "hospital" -> {
                imageView.setImageResource(R.drawable.hospital)
                textView.text = "Rumah Sakit"
                btn_sos.setOnClickListener { btnPress(type = 1) }
            }

            "police" -> {
                imageView.setImageResource(R.drawable.police)
                textView.text = "Polisi"
                btn_sos.setOnClickListener { btnPress(type = 2) }
            }

            "firefighter" -> {
                imageView.setImageResource(R.drawable.firefighter)
                textView.text = "Pemadam Kebakaran"
                btn_sos.setOnClickListener { btnPress(type = 3) }
            }


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun btnPress(type: Int) {
        getLocation { location ->
            if (location != null) {
                longitude = location.longitude
                latitude = location.latitude

                val makeCallRequest = MakeCallRequest(
                    message = messageCall.text.toString(),
                    longitude = longitude,
                    latitude = latitude,
                    type = type
                )
                val token = TokenManager.getToken(this)
                val apiService = token?.let { RetrofitInstance.create(it) }
                if (apiService != null) {
                    apiService.makeCall("Bearer $token", makeCallRequest).enqueue(object :
                        Callback<MakeCallResponse> {
                        override fun onResponse(
                            call: Call<MakeCallResponse>,
                            response: Response<MakeCallResponse>
                        ) {
                            if (response.isSuccessful) {
                                val makeCallResponse = response.body()
                                if (makeCallResponse != null) {
                                    onBackPressedDispatcher
                                    finish()

                                    Toast.makeText(
                                        this@CallEmergencyPage,
                                        "Panggilan darurat telah ditambahkan",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(
                                    this@CallEmergencyPage,
                                    "Panggilan darurat gagal ditambahkan",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<MakeCallResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(
                                this@CallEmergencyPage,
                                "Terjadi kesalahan pada layanan",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
                }
            } else {
                Toast.makeText(
                    this@CallEmergencyPage,
                    "Gagal Mendapatkan Lokasi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            callback(location)
        }.addOnFailureListener {
            callback(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation { location ->
                    if (location != null) {
                        longitude = location.longitude
                        latitude = location.latitude
                    }
                }
            } else {
                Toast.makeText(this@CallEmergencyPage, "Tidak Ada Izin", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
