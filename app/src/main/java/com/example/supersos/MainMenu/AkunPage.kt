package com.example.supersos.MainMenu

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.EditPassword

import com.example.supersos.EditProfile
import com.example.supersos.LoginPage
import com.example.supersos.Models.UserProfile
import com.example.supersos.R
import com.example.supersos.api.RetrofitInstance
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AkunPage : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var frameEdit: FrameLayout
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var btnEditPassword: TextView
    private lateinit var btnLogout: TextView
    private lateinit var btnEdit: ImageButton


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_akun_page, container, false)

        // Inisialisasi semua View yang diperlukan
        profileImageView = view.findViewById(R.id.imageViewProfile)
        fullNameTextView = view.findViewById(R.id.textViewFullName)
//        addressTextView = view.findViewById(R.id.textViewAddress)
        emailTextView = view.findViewById(R.id.textViewEmail)
        phoneTextView = view.findViewById(R.id.textViewPhone)
        btnEdit = view.findViewById(R.id.btn_edit)
        frameEdit = view.findViewById(R.id.frameEdit)
        btnEditPassword = view.findViewById(R.id.btnEditPassword)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        btnEditPassword.setOnClickListener {
            val intent = Intent(context, EditPassword::class.java)
            startActivity(intent)
        }


        frameEdit.setOnClickListener {
            openImagePicker()
        }

        // Panggil fungsi untuk mengambil data profil pengguna
        fetchUserProfile()

        return view
    }
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin Keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun logout() {
        context?.let { TokenManager.clearToken(it) }
        val intent = Intent(context, LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun fetchUserProfile() {
        val token = context?.let { TokenManager.getToken(it) }

        // Memeriksa apakah token tersedia
        if (token != null) {
            val apiService = RetrofitInstance.create(token)

            // Membuat permintaan ke endpoint /api/user/profile
            apiService.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfile> {
                override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                    if (response.isSuccessful) {
                        val profile = response.body()?.values?.get(0)

                        // Memeriksa apakah data profil tidak null
                        if (profile != null) {
                            // Isi data ke TextView sesuai dengan respons API
                            fullNameTextView.text = profile.fullName
//                            addressTextView.text = profile.address
                            emailTextView.text = profile.email
                            phoneTextView.text = profile.phone

                            btnEdit.setOnClickListener {
                                val intent = Intent(context, EditProfile::class.java).apply {
                                    putExtra("fullname", profile.fullName)
                                    putExtra("address", profile.address)
                                    putExtra("email", profile.email)
                                    putExtra("phone", profile.phone)
                                }
                                startActivity(intent)
                            }
                            var imageUrl= profile.pictureUrl

                            Log.d("ErrorCuy", "onResponse: $imageUrl")


//                            profileImageView.setImageURI(imageUrl)

                            Glide.with(requireContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.account)
                                .into(profileImageView)
                        }
                    }
                }

                override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                    // Tangani kegagalan permintaan API
                }
            })
        }
    }
    private fun uploadProfilePicture(imageUri: Uri) {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            val file = File(imageUri.path)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)

            val apiService = RetrofitInstance.create(token)
            apiService.updateProfilePicture("Bearer $token", body).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        profileImageView.setImageURI(imageUri)
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui foto profil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun showConfirmationDialog(imageUri: Uri) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Foto Profil")
            .setMessage("Apakah Anda yakin ingin mengubah foto profil?")
            .setPositiveButton("Ya") { dialog, _ ->
                uploadProfilePicture(imageUri)
                profileImageView.setImageURI(imageUri)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!
            showConfirmationDialog(uri)
            // Use Uri object instead of File to avoid storage permissions

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
