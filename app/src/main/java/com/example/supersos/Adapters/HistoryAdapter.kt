package com.example.supersos.Adapters

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.supersos.CallHistoryDetail
import com.example.supersos.Models.Values
import com.example.supersos.R
import com.example.supersos.Utils.formatDateTime


class HistoryAdapter(private val historyList: List<Values>, private val historyItemClickListener: HistoryItemClickListener) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val coordinate: TextView = itemView.findViewById(R.id.coordinate)
        val datetime: TextView = itemView.findViewById(R.id.tv_datetime)
        val status: TextView = itemView.findViewById(R.id.tv_status)
        val imageView: ImageView = itemView.findViewById(R.id.iconType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        // Set the icon based on callType
        when (item.callType) {
            1 -> holder.imageView.setImageResource(R.drawable.hospital)
            2 -> holder.imageView.setImageResource(R.drawable.police)
            3 -> holder.imageView.setImageResource(R.drawable.firefighter)
        }

        // Set the text values
        holder.message.text = item.message
        holder.coordinate.text = "${item.longitude}, ${item.latitude}"
        holder.datetime.text = formatDateTime(item.appliedAt.toString())


        // Set the background of the status TextView programmatically
        val statusBackground = holder.status.background as GradientDrawable

        when (item.status) {
            1 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.red))
                holder.status.text = "Dibatalkan"
            }

            2 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.green))
                holder.status.text = "Diterima"
            }
            0 -> {
                statusBackground.setColor(holder.itemView.context.getColor(R.color.gray))
                holder.status.text = "Tertunda"
            }
            else -> statusBackground.setColor(holder.itemView.context.getColor(R.color.gray)) // Default
        }

        // Set OnClickListener to navigate to CallHistoryDetail
        holder.itemView.setOnClickListener {
            item.idCall?.let { it1 -> historyItemClickListener.onHistoryItemClick(it1) }
        }
    }

    override fun getItemCount() = historyList.size

    interface HistoryItemClickListener {
        fun onHistoryItemClick(idCall: Int)
    }
}

