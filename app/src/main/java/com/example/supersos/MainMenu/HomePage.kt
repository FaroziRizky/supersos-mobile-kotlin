package com.example.supersos.MainMenu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.CallEmergency.CallEmergencyPage
import com.example.supersos.CurrentLocation.NominatimResponse
import com.example.supersos.CurrentLocation.RetrofitClient
import com.example.supersos.MainPage
import com.example.supersos.Models.LoginResponse
import com.example.supersos.Models.UserNameResponse
import com.example.supersos.R
import com.example.supersos.api.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    private lateinit var userName: TextView
    private lateinit var hospital: CardView
    private lateinit var firefighter: CardView
    private lateinit var police: CardView

    private val locationPermissionRequestCode = 1
    private var longitude: Double = 0.00
    private var latitude: Double = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)
        tvLocation = view.findViewById(R.id.tv_location)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        hospital = view.findViewById(R.id.hospital)
        firefighter = view.findViewById(R.id.firefighter)
        police = view.findViewById(R.id.police)
        userName = view.findViewById(R.id.user_name)

        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            val apiService = RetrofitInstance.create(token)
            apiService.getUserName("Bearer $token").enqueue(object : Callback<UserNameResponse> {
                override fun onResponse(call: Call<UserNameResponse>, response: Response<UserNameResponse>) {
                    if (response.isSuccessful) {
                        val userNameResponse = response.body()
                        Log.d("CekError", "onResponse: "+userNameResponse.toString())
                        if (userNameResponse != null) {
                            userName.text= userNameResponse.values[0].fullname
                        }
                        println("Response: $userNameResponse")
                        // Update UI with the response data
                    } else {
                        println("Error: ${response.errorBody()}")
                        // Handle API call failure
                    }
                }

                override fun onFailure(call: Call<UserNameResponse>, t: Throwable) {
                    t.printStackTrace()
                    // Handle failure
                }
            })
        } else {
            // Handle the case where token is not available
        }

        val emergencyClickListener = View.OnClickListener { view ->
            val emergencyType = when (view.id) {
                R.id.hospital -> "hospital"
                R.id.firefighter -> "firefighter"
                R.id.police -> "police"
                else -> return@OnClickListener
            }
            val intent = Intent(context, CallEmergencyPage::class.java).apply {
                putExtra("emergency_type", emergencyType)
            }
            startActivity(intent)
        }

        hospital.setOnClickListener(emergencyClickListener)
        firefighter.setOnClickListener(emergencyClickListener)
        police.setOnClickListener(emergencyClickListener)



        getLocation()
        return view
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val call = RetrofitClient.nominatimService.reverseGeocode(
                    location.latitude, location.longitude
                )
                call.enqueue(object : Callback<NominatimResponse> {
                    override fun onResponse(
                        call: Call<NominatimResponse>,
                        response: Response<NominatimResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val address = response.body()?.address
                            val village = address?.village ?: ""
                            val county = address?.county ?: ""
                            val city = address?.city ?: ""
                            val locationInfo = buildString {
                                append("${location.latitude}, ${location.longitude}")

                                if (!village.isNullOrEmpty()) {
                                    append(", $village")
                                }
                                if (!county.isNullOrEmpty()) {
                                    append(", $county")
                                }
                                if (!city.isNullOrEmpty()) {
                                    append(", $city")
                                }
                            }
                            tvLocation.text = locationInfo
                        } else {
                            tvLocation.text = "Gagal mendapatkan lokasi"
                        }
                    }

                    override fun onFailure(call: Call<NominatimResponse>, t: Throwable) {
                        tvLocation.text = "Gagal mendapatkan lokasi: ${t.message}"
                    }
                })
            } else {
                longitude = 0.00
                latitude = 0.00
            }
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
                getLocation()
            } else {
                tvLocation.text = "Permission denied"
            }
        }
    }
}
