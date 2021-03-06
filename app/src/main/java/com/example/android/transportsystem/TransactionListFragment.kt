package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class TransactionListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionArrayList: ArrayList<Transaction>
    private lateinit var myAdapter: TransactionAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_transaction_list, container, false)

        recyclerView = v.findViewById(R.id.transactionList_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        transactionArrayList = arrayListOf()

        myAdapter = TransactionAdapter(transactionArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        return v
    }

    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email

        db.collection("transactions").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(document.get("userEmail")!! == email){
                        transactionArrayList.add(document.toObject(Transaction::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
    }

}