package com.example.android.transportsystem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot





class PayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pay, container, false)

        val rootRef = FirebaseFirestore.getInstance()
        val subjectsRef = rootRef.collection("stations")

        val subjects: MutableList<String> = ArrayList()
        subjectsRef.get().
        addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                for (document in documents.result) {
                    subjects.add(document.id)
                }
            } else {
                Log.d("ERROR", "Error reading firebase data", documents.exception)
            }
        }

        val spinner = v.findViewById<Spinner>(R.id.pay_initialstation)

        if (spinner != null) {
            val adapter = ArrayAdapter(v.context,
                android.R.layout.simple_spinner_item, subjects)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long,
                ) {
                    Toast.makeText(activity,
                                subjects[position], Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }


        }

        return v
    }


}