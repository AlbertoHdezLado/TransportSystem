package com.example.android.transportsystem

import android.os.Bundle
import android.util.Log
import android.util.PrintStreamPrinter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore


class PayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pay, container, false)

        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("stations")

        val stations: MutableList<String> = ArrayList()
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

        val initialStationSpinner = v.findViewById<Spinner>(R.id.pay_initialstation)

        if (initialStationSpinner != null) {
            val adapter = ArrayAdapter(this.context!!,
                R.layout.spinner_item, stations)
            initialStationSpinner.adapter = adapter

            initialStationSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Toast.makeText(v.context,
                        getString(R.string.InitialStation) + " " +
                                "" + stations[position], Toast.LENGTH_SHORT).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        // access the items of the list
        val languages = resources.getStringArray(R.array.Languages)

        // access the spinner
        val spinner = v.findViewById<Spinner>(R.id.pay_pay_type)
        if (spinner != null) {
            val adapter = ArrayAdapter(this.context!!,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    Toast.makeText(v.context,
                        getString(R.string.TypeTransport) + " " +
                                "" + languages[position], Toast.LENGTH_SHORT).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        return v
    }


}