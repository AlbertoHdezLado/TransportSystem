package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime


class PayFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var appMoney = 0.0
    var taxMoney = 0.0
    @SuppressLint("SetTextI18n")
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
        subjectsRef.get().addOnCompleteListener { documents ->
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
        subjectsRef2.get().addOnCompleteListener { documents ->
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
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long,
                ) {
                    if (stations[position] != "") {
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

            finalStation.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long,
                ) {
                    if (stations[position] != "") {
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
        val minPrice = 0.1
        val shortestRoute: MutableList<String> = mutableListOf()
        val shortestStops: MutableList<String> = mutableListOf()
        var time = 0
        buttonCalculate.setOnClickListener {
            if (stations[posIni] != "" && stations[posEnd] != "" && posIni != posEnd) {
                shortestRoute.clear()
                shortestStops.clear()
                time = 0
                var opposite = false
                if (posIni > posEnd) {
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
                        if (stationVehicle.contains(station1Temporal) && stationVehicle.contains(
                                station2) && !shortestRoute.contains(vehicles[vehicleStations.values.indexOf(
                                stationVehicle)])
                        ) {
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
                    if (!end) {
                        for (stationVehicle in vehicleStations.values) {
                            if (stationVehicle.contains(station1Temporal) && !shortestRoute.contains(
                                    vehicles[vehicleStations.values.indexOf(stationVehicle)])
                            ) {
                                shortestRoute.add(vehicles[vehicleStations.values.indexOf(
                                    stationVehicle)])
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
                priceText.text = ("%.2f").format(time * minPrice) + " z??"
                println(priceText.text.toString())
                //If the route is the other way around
                if (opposite) {
                    shortestRoute.reverse()
                    shortestStops.reverse()
                    val temporal = posIni
                    posIni = posEnd
                    posEnd = temporal
                }
                println(shortestRoute)
                shortestRoute.forEachIndexed { i, vehicle ->
                    if (i == 0)
                        routeText.text =
                            "${vehicle}: ${shortestStops[i]} --> ${shortestStops[i + 1]} \n"
                    else
                        routeText.text =
                            routeText.text.toString() + "${vehicle}: ${shortestStops[i]} --> ${shortestStops[i + 1]} \n"
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
            val email = FirebaseAuth.getInstance().currentUser?.email
            var currentMoney = 0.0
            val db = FirebaseFirestore.getInstance()
            db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.exists()) {
                            currentMoney = document.get("money").toString().toDouble()
                        } else {
                            Log.d(ContentValues.TAG, "The document doesn't exist.")
                        }
                    }
                }
            //Checks if there is enough money to pay
            Handler().postDelayed({
                if (!routeText.text.isEmpty() && !timeText.text.isEmpty() && !priceText.text.isEmpty() && ((time * minPrice) <= currentMoney)) {
                    val date = LocalDate.now()
                    var dateString = ""
                    val timeIni = LocalTime.now()
                    val stationIni = stations[posIni]
                    val stationEnd = stations[posEnd]
                    val money = time * minPrice
                    var hour = ((timeIni.minute + time) / 60)
                    var min = (timeIni.minute + time) % 60
                    val timeEnd =
                        (if (timeIni.hour + hour < 10) "0${timeIni.hour + hour}" else (timeIni.hour + hour).toString()) +
                                ":" + (if (min < 10) "0${min}" else min.toString()) +
                                ":" + (if (timeIni.second < 10) "0${timeIni.second}" else timeIni.second.toString())
                    val timeStart =
                        (if (timeIni.hour < 10) "0${timeIni.hour}" else timeIni.hour.toString()) +
                                ":" + (if (timeIni.minute < 10) "0${timeIni.minute}" else timeIni.minute.toString()) +
                                ":" + (if (timeIni.second < 10) "0${timeIni.second}" else timeIni.second.toString())

                    if (date.monthValue < 10) {
                        if (date.dayOfMonth < 10) {
                            dateString =
                                date.year.toString() + 0 + date.monthValue.toString() + 0 + date.dayOfMonth.toString() + timeStart.replace(":", "")

                        } else {
                            dateString =
                                date.year.toString() + 0 + date.monthValue.toString() + date.dayOfMonth.toString() + timeStart.replace(":", "")
                        }
                    } else {
                        dateString =
                            date.year.toString() + date.monthValue.toString() + date.dayOfMonth.toString() + timeStart.replace(":", "")
                    }

                    val dateLong = dateString.toLong()


                    //Create the document to add
                    val journey: MutableMap<String, Any> = mutableMapOf()
                    journey["date"] = dateLong
                    journey["initialStation"] = stationIni
                    journey["finalStation"] = stationEnd
                    journey["money"] = money
                    journey["time"] = time
                    journey["timeStart"] = timeStart
                    journey["timeEnd"] = timeEnd
                    journey["userEmail"] = email.toString()
                    journey["vehicles"] = shortestRoute
                    journey["stops"] = shortestStops

                    //add the document
                    //Save user data in firebase
                    val UniqueID = rootRef.collection("journeys").document().id
                    val journeyBD = Journey(UniqueID,
                        email,
                        dateLong,
                        shortestStops,
                        shortestRoute,
                        stationIni,
                        stationEnd,
                        money.toLong(),
                        time.toLong(),
                        timeStart,
                        timeEnd)
                    val journeydb = FirebaseFirestore.getInstance()
                    val journeyRef = journeydb.collection("journeys")

                    journeyRef.document(UniqueID).set(journeyBD)
                    Toast.makeText(
                        activity,
                        "Ticket paid successfully, with:\n Start station: " + stations[posIni]
                                + "\n End station: " + stations[posEnd], Toast.LENGTH_SHORT
                    ).show()
                    //Changes money value to pay
                    val email = FirebaseAuth.getInstance().currentUser?.email
                    val usersRef = db.collection("users")
                    usersRef.whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                if (document.exists()) {
                                    db.collection("users")
                                        .document(document.id)
                                        .update(
                                            mapOf(
                                                "money" to (document.getDouble("money")
                                                !!.minus(money))
                                            )
                                        )
                                    (activity as MainActivity).updateUserMoney()
                                    moneyDivision(money)
                                    findNavController().popBackStack()
                                } else {
                                    Log.d(ContentValues.TAG, "The document doesn't exist.")
                                }
                            }
                        }

                        .addOnFailureListener {
                            Toast.makeText(
                                activity,
                                "Journey failed to buy", Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {
                    Toast.makeText(
                        activity, "Missing Data or money to Pay", Toast.LENGTH_SHORT
                    ).show()
                }
            }, 200)
        }

        return v
    }


    /*
     *   Calculate the Int value of time from String values
     */
    private fun stringToInt(s: String): Int {
        if (s == "0") {
            return 0
        } else if (s == "1") {
            return 1
        } else if (s == "2") {
            return 2
        } else {
            return 3
        }
    }
    /*
     * Divides the money in beneficts and taxes
     */
    private fun moneyDivision(m: Double){
        appMoney += (m * 0.9)
        taxMoney += (m * 0.1)
    }

}