package com.example.android.transportsystem

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime


class PayFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pay, container, false)

        //Get stations names from Firebase.
        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("stations")

        val stations: MutableList<String> = mutableListOf()
        subjectsRef.get().
        addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                for (document in documents.result) {
                    stations.add(document.id)
                }
            } else {
                Log.d("ERROR", "Error reading firebase data", documents.exception)
            }
        }
        stations.add("")
        //Get vehicles information from firebase
        val vehicleStations: MutableMap<String, MutableList<String>> = mutableMapOf()
        val vehicleTime: MutableMap<String, MutableList<String>> = mutableMapOf()
        val vehicles: MutableList<String> = mutableListOf()

        val subjectsRef2 = rootRef.collection("vehicles")
        subjectsRef2.get().
        addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                for (document in documents.result) {
                    vehicles.add(document.id)
                    vehicleStations[document.id] = document.get("stations") as MutableList<String>
                    vehicleTime[document.id] = document.get("time") as MutableList<String>
                }
            } else {
                Log.d("ERROR", "Error reading firebase data", documents.exception)
            }
        }

        //Configures the spinners and stores the desired stations.
        val initialStation = v.findViewById<Spinner>(R.id.pay_initialstation)
        val finalStation = v.findViewById<Spinner>(R.id.pay_finalstation)
        var posIni = 0
        var posEnd = 0
        if (initialStation != null) {
            val adapter = ArrayAdapter(activity?.applicationContext!!,
                android.R.layout.simple_spinner_dropdown_item, stations)
            initialStation.adapter = adapter
            //adapter.notifyDataSetChanged()

            initialStation.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    if(stations[position] != "") {
                        posIni = position
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }

            }
        }
        if (finalStation != null) {
            val adapter = ArrayAdapter(
                activity?.applicationContext!!,
                android.R.layout.simple_spinner_dropdown_item, stations
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            finalStation.adapter = adapter
            //adapter.notifyDataSetChanged()

            finalStation.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if(stations[position] != "") {
                        posEnd = position
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }

            }
        }

        //Calcules the best route
        val buttonCalculate = v.findViewById<Button>(R.id.pay_calculateRoute)
        val timeText = v.findViewById<TextView>(R.id.pay_time)
        val priceText = v.findViewById<TextView>(R.id.pay_price)
        val routeText = v.findViewById<TextView>(R.id.pay_routes)
        val stopsText = v.findViewById<TextView>(R.id.pay_stops)
        val minPrice = 0.1
        val shortestRoute: MutableList<String> = mutableListOf()
        val shortestStops: MutableList<String> = mutableListOf()
        var time = 0
        buttonCalculate.setOnClickListener {
            if (stations[posIni] != "" && stations[posEnd] != "" && posIni != posEnd) {
                shortestRoute.clear()
                shortestStops.clear()
                var opposite = false
                if(posIni > posEnd){
                    opposite = true
                    val temporal = posIni
                    posIni = posEnd
                    posEnd = temporal
                }
                val station2 = stations[posEnd]
                var station1Temporal = stations[posIni]
                var posTemporal = posIni
                var posTemporalEnd = posEnd
                var end = false
                //Algorithm
                shortestStops.add(station1Temporal)
                while (!end) {
                    //If stations are in the same route
                    for (stationVehicle in vehicleStations.values) {
                        if (stationVehicle.contains(station1Temporal) && stationVehicle.contains(station2) && !shortestRoute.contains(vehicles[vehicleStations.values.indexOf(stationVehicle)])) {
                            shortestRoute.add(vehicles[vehicleStations.values.indexOf(stationVehicle)])
                            shortestStops.add(station2)
                            posTemporal = stationVehicle.indexOf(station1Temporal)
                            posTemporalEnd = stationVehicle.indexOf(station2)
                            while (posTemporal <= posTemporalEnd) {
                                time += stringToInt(
                                    vehicleTime[vehicles[vehicleStations.values.indexOf(
                                        stationVehicle
                                    )]]!![posTemporal]
                                )
                                posTemporal++
                            }
                            end = true
                            break
                        }
                    }
                    //When there are transbords implicated
                    if(!end) {
                        for (stationVehicle in vehicleStations.values) {
                            if(stationVehicle.contains(station1Temporal) && !shortestRoute.contains(vehicles[vehicleStations.values.indexOf(stationVehicle)])) {
                                shortestRoute.add(vehicles[vehicleStations.values.indexOf(stationVehicle)])
                                posTemporal = stationVehicle.indexOf(station1Temporal)
                                station1Temporal = stationVehicle.last()
                                shortestStops.add(station1Temporal)
                                while (posTemporal <= stationVehicle.indexOf(stationVehicle.last())) {
                                    time += stringToInt(
                                        vehicleTime[vehicles[vehicleStations.values.indexOf(
                                            stationVehicle
                                        )]]!![posTemporal]
                                    )
                                    posTemporal++
                                }
                                break
                            }

                        }
                    }
                }
                //Shows the values
                timeText.text = time.toString() + " min"
                priceText.text = ("%.2f").format(time * minPrice) + " zł"
                //If the route is the other way around
                if(opposite){
                    shortestRoute.reverse()
                    shortestStops.reverse()
                    val temporal = posIni
                    posIni = posEnd
                    posEnd = temporal
                }
                if(shortestRoute.size > 3){
                    routeText.text = "${shortestRoute.subList(0, 3)} \n ${shortestRoute.subList(3, shortestRoute.size)}"
                } else {
                    routeText.text = "$shortestRoute"
                }
                if(shortestStops.size > 2){
                    stopsText.text = "${shortestStops.subList(0, 2)} \n ${shortestStops.subList(2, shortestStops.size)}"
                } else if (shortestStops.size > 4) {
                    stopsText.text = "${shortestStops.subList(0, 2)} \n ${shortestStops.subList(2, 4)} \n ${shortestStops.subList(4, shortestStops.size)}"
                } else if(shortestStops.size > 6){
                    stopsText.text = "${shortestStops.subList(0, 2)} \n ${shortestStops.subList(2, 4)} \n ${shortestStops.subList(4, 6)} \n ${shortestStops.subList(6, shortestStops.size)}"
                } else {
                    stopsText.text = "$shortestStops"
                }
            } else {
                Toast.makeText(
                    activity,
                    "Introduce new appropriate values", Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Pay for the tickets
        val buttonPay = v.findViewById<Button>(R.id.pay_payButton)
        buttonPay.setOnClickListener {
            //Show confirm message and upload journey to database
            if(!routeText.text.isEmpty() && !timeText.text.isEmpty() && !priceText.text.isEmpty()) {
                val date = LocalDate.now()
                val timeIni = LocalTime.now()
                val stationIni = stations[posIni]
                val stationEnd = stations[posEnd]
                val money = time * minPrice
                var hour = ((timeIni.minute + time) / 60)
                var min = (timeIni.minute + time) % 60
                val timeEnd = (timeIni.hour + hour).toString() + ":" + min.toString() + ":" + timeIni.second.toString()
                val email = FirebaseAuth.getInstance().currentUser?.email

                //Create the document to add
                val db = FirebaseFirestore.getInstance()
                val journey: MutableMap<String, Any> = mutableMapOf()
                journey["date"] = date.toString()
                journey["initialStation"] = stationIni
                journey["finalStation"] = stationEnd
                journey["id"] = ""
                journey["money"] = money
                journey["time"] = time
                journey["timeStart"] = (timeIni.hour).toString() + ":" + (timeIni.minute).toString() + ":" + (timeIni.second).toString()
                journey["timeEnd"] = timeEnd
                journey["userEmail"] = email.toString()
                journey["vehicles"] = shortestRoute
                journey["stops"] = shortestStops

                //add the document
                db.collection("journeys")
                    .add(journey)
                    .addOnSuccessListener {
                        db.collection("journeys")
                            .document(it.id)
                            .update( mapOf(
                                "id" to it.id
                            ))
                        Toast.makeText(
                            activity,
                            "Ticket paid successfully, with:\n Start station: " + stations[posIni]
                                    + "\n End station: " + stations[posEnd], Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "Journey failed to buy", Toast.LENGTH_SHORT
                        ).show()
                    }

            } else {
                Toast.makeText(
                    activity, "Missing Data to Pay", Toast.LENGTH_SHORT
                ).show()
            }
        }

        return v
    }


    /*
     *   Calculate the Int value of time from String values
     */
    private fun stringToInt(s: String): Int{
        if(s == "0") {
            return 0
        } else if(s == "1"){
            return 1
        } else if(s == "2"){
            return 2
        } else{
            return 3
        }
    }

}