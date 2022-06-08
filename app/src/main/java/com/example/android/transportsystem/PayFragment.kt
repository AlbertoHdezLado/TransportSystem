package com.example.android.transportsystem

import android.os.Bundle
import android.util.Log
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

        val spinner = v.findViewById<Spinner>(R.id.pay_initialstation)

        if (spinner != null) {
            val adapter = ArrayAdapter(activity?.applicationContext!!,
                android.R.layout.simple_spinner_item, stations)
            spinner.adapter = adapter
        }

        /*spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                initialStationText.text = stations[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }*/
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinner = activity?.findViewById<Spinner>(R.id.pay_initialstation)
        val stations: MutableList<String> = ArrayList()

        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("stations")

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

        spinner?.adapter = ArrayAdapter(activity?.applicationContext!!, R.layout.support_simple_spinner_dropdown_item, stations)

        super.onViewCreated(view, savedInstanceState)
    }


}