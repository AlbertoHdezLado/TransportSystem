package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JourneyAdapter (private val journeyList : ArrayList<Journey>) : RecyclerView.Adapter<JourneyAdapter.JourneyHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): JourneyAdapter.JourneyHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.journey_card, parent, false)

        return JourneyHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: JourneyAdapter.JourneyHolder, position: Int) {
        val journey: Journey = journeyList[position]
        holder.date.text = journey.date
        holder.id.text = journey.id
        holder.stations.text = journey.initialStation + " -> " + journey.finalStation
        holder.time.text = journey.time.toString()
        holder.money.text = journey.money.toString()
        holder.kind.text = journey.kind
    }

    override fun getItemCount(): Int {
        return journeyList.size
    }

    public class JourneyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.journeycard_date)
        val id = itemView.findViewById<TextView>(R.id.journeycard_id)
        val stations = itemView.findViewById<TextView>(R.id.journeycard_id)
        val time = itemView.findViewById<TextView>(R.id.journeycard_time)
        val money = itemView.findViewById<TextView>(R.id.journeycard_money)
        val kind = itemView.findViewById<TextView>(R.id.journeycard_kind)

    }
}