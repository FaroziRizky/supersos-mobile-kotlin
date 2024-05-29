package com.example.supersos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.MainMenu.HomePage
import com.example.supersos.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonUpdateProfile: TextView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Inisialisasi view
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile)
        btnBack= findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        editTextFullName.setText(intent.getStringExtra("fullname"))
        editTextAddress.setText(intent.getStringExtra("address"))
        editTextPhone.setText(intent.getStringExtra("phone"))
        editTextEmail.setText(intent.getStringExtra("email"))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonUpdateProfile.setOnClickListener {
            updateUserProfile()
        }
    }

    private fun updateUserProfile() {
        val token = TokenManager.getToken(this)

        // Ambil data dari input fields
        val fullName = editTextFullName.text.toString()
        val address = editTextAddress.text.toString()
        val phone = editTextPhone.text.toString()
        val email = editTextEmail.text.toString()

        if (token != null) {

            val apiService = token?.let { RetrofitInstance.create(it) }

            val requestBody = mapOf(
                "fullname" to fullName,
                "address" to address,
                "phone" to phone,
                "email" to email
            )

            val editProfile: Call<Void?>? =
                apiService!!.updateUserProfile(token, requestBody)

            if (editProfile != null) {
                editProfile.enqueue(object : Callback<Void?>  {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
//                                val intent = Intent(this@EditProfile, HomePage::class.java)
//                                startActivity(intent)
                                Toast.makeText(this@EditProfile, "Berhasil Memperbarui Profile", Toast.LENGTH_SHORT).show()
                                onBackPressed()
                            } else {
                                Toast.makeText(this@EditProfile, "Gagal Memperbarui Profile", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@EditProfile, "Failed to update profile ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Toast.makeText(this@EditProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
