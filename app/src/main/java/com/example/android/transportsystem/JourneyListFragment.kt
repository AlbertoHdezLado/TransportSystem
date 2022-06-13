package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class JourneyListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var journeyArrayList: ArrayList<Journey>
    private lateinit var myAdapter: JourneyAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_journey_list, container, false)

        recyclerView = v.findViewById(R.id.journeyList_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        journeyArrayList = arrayListOf()

        myAdapter = JourneyAdapter(journeyArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        return v
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email

        db.collection("journeys").whereEqualTo("userEmail", email).addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        journeyArrayList.add(dc.document.toObject(Journey::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }
}