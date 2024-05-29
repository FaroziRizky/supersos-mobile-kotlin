package com.example.supersos.MainMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supersos.Adapters.InstancesAdapter
import com.example.supersos.Models.Instance
import com.example.supersos.R

class InstancesPage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instances_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_instances)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Dummy data
        val instanceList = listOf(
            Instance(1, "Instance One", "1234 Address Lane"),
            Instance(2, "Instance Two", "5678 Address Blvd"),
            Instance(3, "Instance Three", "91011 Address St")
        )

        val adapter = InstancesAdapter(instanceList)
        recyclerView.adapter = adapter
    }


}
