package com.example.supersos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.CallEmergency.CallEmergencyPage
import com.example.supersos.Models.LoginRequest
import com.example.supersos.Models.LoginResponse
import com.example.supersos.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {
    private lateinit var containerMessage: LinearLayout
    private lateinit var tvMessage: TextView
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_page)
        containerMessage = findViewById(R.id.containerMessage)
        tvMessage = findViewById(R.id.tv_message)
        val showPasswordButton = findViewById<ImageButton>(R.id.imageButtonShowPassword)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)

        val registerButton = findViewById<Button>(R.id.buttonRegister)
        registerButton.setOnClickListener {

            val intent = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(intent)
        }

        emailEditText.setText("satriayoga@gmail.com")
        passwordEditText.setText("1")

        showPasswordButton.setOnClickListener {
            if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                passwordEditText.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.drawable.ic_visibility_off)
            } else {

                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.drawable.ic_visibility)
            }
        }

        findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            val loginRequest =
                LoginRequest(email = "${emailEditText.text}", password = "${passwordEditText.text}")

            RetrofitInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val statusCode = response.code()
                        if (statusCode == 200 && loginResponse != null) {
                            TokenManager.saveToken(this@LoginPage, loginResponse.token)
                            startActivity(Intent(this@LoginPage, MainPage::class.java))
                            finish()

                            Toast.makeText(this@LoginPage, "Login Berhasil", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        if (response.code() == 403) {
                            showMessage("Email atau Password Salah")
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showMessage("Terjadi Kesalahan Pada Server")
                    Log.d("CekError", "onFailure: $t")
                }
            })


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


    private fun showMessage(message: String) {
        tvMessage.text = message
        tvMessage.visibility = VISIBLE
        handler.postDelayed({
            hideMessage()
        }, 3000)
    }

    private fun hideMessage() {
        tvMessage.visibility = GONE
    }
}
