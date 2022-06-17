package com.example.android.transportsystem

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.*

class JourneyCheckedActivity : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_checked)
        val journeyId = intent.extras?.getString("journeyId")

        val idText = findViewById<TextView>(R.id.journeychecked_id)
        val userEmailText = findViewById<TextView>(R.id.journeychecked_useremail)
        val dateText = findViewById<TextView>(R.id.journeychecked_date)
        val moneyText = findViewById<TextView>(R.id.journeychecked_money)
        val routeText = findViewById<TextView>(R.id.journeychecked_route)

        db.collection("journeys").document(journeyId!!).get().addOnSuccessListener {
            val currentDate = it["date"].toString().toCharArray()
            dateText.text = "${currentDate[0]}${currentDate[1]}${currentDate[2]}${currentDate[3]}-${currentDate[4]}${currentDate[5]}-${currentDate[6]}${currentDate[7]}\n${it["timeStart"]} - ${it["timeEnd"]}"
            idText.text = journeyId
            moneyText.text = "${it["money"]} zł"
            userEmailText.text = "${it["userEmail"]}"
            val stations = it["stops"] as ArrayList<*>
            val vehicles = it["vehicles"] as ArrayList<*>
            vehicles.forEachIndexed { i, vehicle ->
                if (i == 0)
                    routeText.text = "${vehicle}: ${stations[i]} --> ${stations[i+1]} \n"
                else
                    routeText.text = routeText.text.toString() + "${vehicle}: ${stations[i]} --> ${stations[i+1]} \n"
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }
}