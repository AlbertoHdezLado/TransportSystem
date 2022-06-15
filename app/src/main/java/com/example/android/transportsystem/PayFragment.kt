package com.example.android.transportsystem

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.FileOutputStream
import androidmads.library.qrgenearator.QRGEncoder

import android.graphics.Bitmap
import com.google.zxing.WriterException

import android.R.dimen
import android.net.Uri

import androidmads.library.qrgenearator.QRGContents
import com.google.firebase.storage.UploadTask

import com.google.android.gms.tasks.OnSuccessListener

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener
import java.io.ByteArrayOutputStream


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
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long,
                ) {
                    if(stations[position] != "") {
                        Toast.makeText(
                            activity,
                            "Initial station: " + stations[position], Toast.LENGTH_SHORT
                        ).show()
                    }
                    posIni = position
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
                    parent: AdapterView<*>, view: View, position: Int, id: Long,
                ) {
                    if(stations[position] != "") {
                        Toast.makeText(
                            activity,
                            "End station: " + stations[position], Toast.LENGTH_SHORT
                        ).show()
                    }
                    posEnd = position
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
        buttonCalculate.setOnClickListener {
            if (stations[posIni] != "" && stations[posEnd] != "" && posIni != posEnd) {
                val shortestRoute: MutableList<String> = mutableListOf()
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
                var time = 0
                var end = false
                //Algorithm
                while (!end) {
                    //If stations are in the same route
                    for (stationVehicle in vehicleStations.values) {
                        if (stationVehicle.contains(station1Temporal) && stationVehicle.contains(station2) && !shortestRoute.contains(vehicles[vehicleStations.values.indexOf(stationVehicle)])) {
                            shortestRoute.add(vehicles[vehicleStations.values.indexOf(stationVehicle)])
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
                }
                if(shortestRoute.size > 3){
                    routeText.text = "\n ${shortestRoute.subList(0, 3)} \n ${shortestRoute.subList(3, shortestRoute.size)}"
                } else {
                    routeText.text = "$shortestRoute"
                }
                Toast.makeText(
                    activity,
                    shortestRoute.toString(), Toast.LENGTH_SHORT
                ).show()
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
            //Shows message
            if(!routeText.text.isEmpty() && !timeText.text.isEmpty() && !priceText.text.isEmpty()){
                Toast.makeText(
                    activity,
                    "Ticket paid successfully, with:\n Start station: " + stations[posIni]
                            + "\n End station: " + stations[posEnd], Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    activity, "Missing Data to Pay", Toast.LENGTH_SHORT
                ).show()
            }
        }

        return v
    }

    fun stringToInt(s: String): Int{
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

    fun generateQr(id: String){
        val qrgEncoder = QRGEncoder(id, null, QRGContents.Type.TEXT, 25)
        val bitmap = qrgEncoder.encodeAsBitmap()

        val storage = FirebaseStorage.getInstance()

        // Create a storage reference from our app
        val storageRef = storage.getReferenceFromUrl("gs://transportsystem-b236d.appspot.com")

        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("$id.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }



}