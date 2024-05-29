package com.example.supersos.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supersos.Models.Instance
import com.example.supersos.R

class InstancesAdapter(private val instanceList: List<Instance>) :
    RecyclerView.Adapter<InstancesAdapter.InstanceViewHolder>() {

    class InstanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val instanceName: TextView = itemView.findViewById(R.id.tv_instance_name)
        val address: TextView = itemView.findViewById(R.id.tv_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_instance, parent, false)
        return InstanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstanceViewHolder, position: Int) {
        val instance = instanceList[position]
        holder.instanceName.text = instance.instancesName
        holder.address.text = instance.address
    }

    override fun getItemCount() = instanceList.size
}
