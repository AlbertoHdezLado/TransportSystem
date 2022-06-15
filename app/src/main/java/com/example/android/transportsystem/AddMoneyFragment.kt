package com.example.android.transportsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class AddMoneyFragment : Fragment() {
    /*private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_add_money, container, false)
        lateinit var userId: String
        var money by Delegates.notNull<Int>()
        val addMoneyButton = v.findViewById<Button>(R.id.addm_addMoneyButton)
        addMoneyButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToPayFragment()
            findNavController().navigate(action)
        }
        return v
    }*/
}
