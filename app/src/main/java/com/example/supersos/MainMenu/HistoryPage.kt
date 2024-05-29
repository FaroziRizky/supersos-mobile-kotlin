package com.example.supersos.MainMenu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.supersos.Adapters.HistoryAdapter
import com.example.supersos.ApiService.TokenManager
import com.example.supersos.CallHistoryDetail
import com.example.supersos.Models.HistoryItem
import com.example.supersos.Models.Values
import com.example.supersos.R
import com.example.supersos.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryPage : Fragment(), HistoryAdapter.HistoryItemClickListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var historyList: MutableList<Values>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var apiMessageContainer: LinearLayout

    companion object {
        private const val REQUEST_CODE_REFRESH_HISTORY = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_page, container, false)

        apiMessageContainer = view.findViewById(R.id.apiMessageContainer)
        historyList = mutableListOf()
        recyclerView = view.findViewById(R.id.recycler_view_history)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = HistoryAdapter(historyList, this)
        recyclerView.adapter = adapter

        // Inisialisasi SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)

        // Set listener untuk menangani gestur swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            // Lakukan refresh data
            fetchData()
        }

        fetchData()

        return view
    }

    private fun fetchData() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            val apiService = RetrofitInstance.create(token)
            apiService.getHistoryALl("Bearer $token").enqueue(object : Callback<HistoryItem> {
                override fun onResponse(call: Call<HistoryItem>, response: Response<HistoryItem>) {
                    if (response.isSuccessful) {
                        val historyResponse = response.body()
                        if (historyResponse != null) {
                            historyList.clear()
                            historyList.addAll(historyResponse.values)
                            adapter.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                            if (historyResponse.values.size==0){
                                apiMessageContainer.visibility=View.VISIBLE
                            }
                        }
                    } else {
                        Log.e("HistoryPage", "Error: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<HistoryItem>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("HistoryPage", "API call failed")
                }
            })
        } else {
            Log.e("HistoryPage", "Token is not available")
        }


    }
    // Fungsi untuk memulai aktivitas CallHistoryDetail dengan startActivityForResult
    override fun onHistoryItemClick(idCall: Int) {
        val intent = Intent(activity, CallHistoryDetail::class.java)
        intent.putExtra("id_call", idCall)
        startActivityForResult(intent, REQUEST_CODE_REFRESH_HISTORY)
    }

    // Fungsi untuk menangani hasil dari CallHistoryDetail
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_REFRESH_HISTORY && resultCode == Activity.RESULT_OK) {
            // Lakukan refresh halaman
            fetchData()
        }
    }
}
