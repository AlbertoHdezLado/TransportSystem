package com.example.android.transportsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

class JourneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_journey, container, false)

        val dateText = v.findViewById<TextView>(R.id.journey_date)
        val idText = v.findViewById<TextView>(R.id.journey_id)
        val initialStationText = v.findViewById<TextView>(R.id.journey_initialstation)
        val finalStationText = v.findViewById<TextView>(R.id.journey_finalstation)
        val timeText = v.findViewById<TextView>(R.id.journey_time)
        val moneyText = v.findViewById<TextView>(R.id.journey_money)
        val vehiclesText = v.findViewById<TextView>(R.id.journey_vehicles)

        dateText.text = requireArguments().getString("date")
        idText.text = requireArguments().getString("id")
        initialStationText.text = requireArguments().getString("initialStation")
        finalStationText.text = requireArguments().getString("finalStation")
        timeText.text = requireArguments().getLong("time").toString()
        moneyText.text = requireArguments().getLong("money").toString()
        vehiclesText.text = requireArguments().getStringArrayList("vehicles").toString()

        return v
    }
}