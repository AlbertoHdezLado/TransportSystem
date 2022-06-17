package com.example.android.transportsystem

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

class AddMoneyFragment : Fragment() {
    /*private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")*/
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_add_money, container, false)

        //Gets the buttons and inserted data
        val quantityText = v.findViewById<EditText>(R.id.addm_addQuantityNumber)
        val cardNumberText = v.findViewById<EditText>(R.id.addm_cardNumberNumber)
        val cardHolderNameText = v.findViewById<EditText>(R.id.addm_cardHolderNameName)
        val dueDateDate = v.findViewById<EditText>(R.id.addm_dueDateDate)
        val backNumberText = v.findViewById<EditText>(R.id.addm_backNumberNumber)
        val addMoneyButton = v.findViewById<Button>(R.id.addm_addMoneyButton)


        addMoneyButton.setOnClickListener {
            db = FirebaseFirestore.getInstance()
            //If credit card is valid, add the data
            if (!cardNumberText.text.isEmpty() && cardNumberText.text.length == 16 && cardNumberText.text.matches(
                    "[0-9]+".toRegex())
                && !cardHolderNameText.text.isEmpty()
                && !dueDateDate.text.isEmpty() && dueDateDate.text.length == 5 && dueDateDate.text.matches(
                    "[0-9/]+".toRegex())
                && !backNumberText.text.isEmpty() && backNumberText.text.length == 3 && backNumberText.text.matches(
                    "[0-9]+".toRegex())
            ) {
                //Changes money value
                val email = FirebaseAuth.getInstance().currentUser?.email
                val usersRef = db.collection("users")
                usersRef.whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if (document.exists()) {
                                db.collection("users")
                                    .document(document.id)
                                    .update(mapOf(
                                        "money" to (document.getDouble("money")
                                        !!.plus(quantityText.text.toString().toInt()))
                                    ))
                                (activity as MainActivity).updateUserMoney()
                                findNavController().popBackStack()
                            } else {
                                Log.d(ContentValues.TAG, "The document doesn't exist.")
                            }
                        }
                    }
                //Data to add new transaction
                val date = LocalDate.now()
                var dateString = ""
                val timeIni = LocalTime.now()
                val timeStart =
                    (if (timeIni.hour < 10) "0${timeIni.hour}" else timeIni.hour.toString()) +
                            (if (timeIni.minute < 10) "0${timeIni.minute}" else timeIni.minute.toString()) +
                            (if (timeIni.second < 10) "0${timeIni.second}" else timeIni.second.toString())

                if (date.monthValue < 10) {
                    if (date.dayOfMonth < 10) {
                        dateString =
                            date.year.toString() + 0 + date.monthValue.toString() + 0 + date.dayOfMonth.toString() + timeStart

                    } else {
                        dateString =
                            date.year.toString() + 0 + date.monthValue.toString() + date.dayOfMonth.toString() + timeStart
                    }
                } else {
                    dateString =
                        date.year.toString() + date.monthValue.toString() + date.dayOfMonth.toString() + timeStart
                }
                val transaction: MutableMap<String, Any> = mutableMapOf()
                transaction["date"] = dateString.toLong()
                transaction["id"] = ""
                transaction["money"] = quantityText.text.toString().toLong()
                transaction["userEmail"] = email.toString()

                //add the transaction
                db.collection("transactions")
                    .add(transaction)
                    .addOnSuccessListener {
                        db.collection("transactions")
                            .document(it.id)
                            .update(mapOf(
                                "id" to it.id
                            ))
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "Failed to add the Credit Card", Toast.LENGTH_SHORT
                        ).show()
                    }
            //If data is incorrect
            } else {
                Toast.makeText(
                    activity,
                    "Data not correct.\n Try again.", Toast.LENGTH_SHORT
                ).show()
            }
        }
        return v
    }

}
