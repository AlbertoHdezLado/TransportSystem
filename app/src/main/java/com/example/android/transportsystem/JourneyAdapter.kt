package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class JourneyAdapter (private val journeyList : ArrayList<Journey>) : RecyclerView.Adapter<JourneyAdapter.JourneyHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): JourneyAdapter.JourneyHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.journey_card, parent, false)

        return JourneyHolder(itemView, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: JourneyAdapter.JourneyHolder, position: Int) {
        val journey: Journey = journeyList[position]
        println(journey)
        holder.date.text = journey.date
        holder.id.text = journey.id
        holder.initialStation.text = journey.initialStation
        holder.finalStation.text = journey.finalStation
        holder.time.text = journey.time.toString()
        holder.money.text = journey.money.toString()
        holder.vehicles.text = journey.vehicles?.joinToString(", ")
    }

    override fun getItemCount(): Int {
        return journeyList.size
    }

    public class JourneyHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.journeycard_date)
        val id = itemView.findViewById<TextView>(R.id.journeycard_id)
        val initialStation = itemView.findViewById<TextView>(R.id.journeycard_initialstations)
        val finalStation = itemView.findViewById<TextView>(R.id.journeycard_finalstations)
        val time = itemView.findViewById<TextView>(R.id.journeycard_time)
        val money = itemView.findViewById<TextView>(R.id.journeycard_money)
        val vehicles = itemView.findViewById<TextView>(R.id.journeycard_vehicles)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition )
            }
        }
    }
}