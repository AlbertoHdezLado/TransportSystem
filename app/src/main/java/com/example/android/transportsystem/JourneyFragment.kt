package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder

class JourneyFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_journey, container, false)

        val qrcodeImage = v.findViewById<ImageView>(R.id.qrcodeimage)
        val dateText = v.findViewById<TextView>(R.id.journey_date)
        val idText = v.findViewById<TextView>(R.id.journey_id)
        val moneyText = v.findViewById<TextView>(R.id.journey_money)
        val routeText = v.findViewById<TextView>(R.id.journey_route)

        dateText.text = "${requireArguments().getString("date")}\n${requireArguments().getString("timeStart")} - ${requireArguments().getString("timeEnd")}"
        idText.text = requireArguments().getString("id")
        moneyText.text = requireArguments().getLong("money").toString()

        val vehicles = requireArguments().getStringArrayList("vehicles")
        val stops = requireArguments().getStringArrayList("stops")

        vehicles?.forEachIndexed { i, vehicle ->
            if (i == 0)
                routeText.text = "${vehicle}: ${stops?.get(i)} --> ${stops?.get(i+1)} \n"
            else
                routeText.text = routeText.text.toString() + "${vehicle}: ${stops?.get(i)} --> ${stops?.get(i+1)} \n"
        }

        val qrgEncoder = QRGEncoder(requireArguments().getString("id"), null, QRGContents.Type.TEXT, 750)
        val bitmap = qrgEncoder.encodeAsBitmap()
        qrcodeImage.setImageBitmap(bitmap)

        return v
    }
}