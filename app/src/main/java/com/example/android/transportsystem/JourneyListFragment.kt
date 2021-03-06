package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

        myAdapter.setOnClickListener(object : JourneyAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                findNavController().navigate(R.id.action_journeyListFragment_to_journeyFragment, Bundle().apply {
                    putString("id" ,journeyArrayList[position].id)
                    putLong("date" ,journeyArrayList[position].date!!)
                    putString("timeStart" ,journeyArrayList[position].timeStart)
                    putString("timeEnd" ,journeyArrayList[position].timeEnd)
                    putString("initialStation" ,journeyArrayList[position].initialStation)
                    putString("finalStation" ,journeyArrayList[position].finalStation)
                    putLong("time" ,journeyArrayList[position].time!!)
                    putLong("money" ,journeyArrayList[position].money!!)
                    putStringArrayList("vehicles" , journeyArrayList[position].vehicles as ArrayList<String>)
                    putStringArrayList("stops" , journeyArrayList[position].stops as ArrayList<String>)
                })
            }
        })

        recyclerView.adapter = myAdapter

        EventChangeListener()

        return v
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email

        db.collection("journeys").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.get("userEmail")!! == email){
                        journeyArrayList.add(document.toObject(Journey::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
    }
}