package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.util.*
import kotlin.properties.Delegates

class AddMoneyFragment : Fragment() {
    /*private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")*/
    private lateinit var db: FirebaseFirestore
    private val backNumber: MutableMap<String, String> = mutableMapOf()
    private val cardHolderName: MutableMap<String, String> = mutableMapOf()
    private val cardNumber: MutableMap<String, String> = mutableMapOf()
    private val dueDate: MutableMap<String, String> = mutableMapOf()
    private val pin: MutableMap<String, String> = mutableMapOf()

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
        val passwordText = v.findViewById<EditText>(R.id.addm_passwordNumber)
        val addMoneyButton = v.findViewById<Button>(R.id.addm_addMoneyButton)
        getCreditCardsData()


        addMoneyButton.setOnClickListener {
            db = FirebaseFirestore.getInstance()
            val dbCollection = db.collection("creditCards")
            var contained = false
            dbCollection.get().
            addOnCompleteListener { documents ->
                if (documents.isSuccessful) {
                    for (document in documents.result) {
                        if(cardNumber[document.id]!!.equals(cardNumberText.text.toString())
                            && cardHolderName[document.id]!!.equals(cardHolderNameText.text.toString())
                            && dueDate[document.id]!!.equals(dueDateDate.text.toString())
                            && backNumber[document.id]!!.equals(backNumberText.text.toString())
                            && pin[document.id]!!.equals(passwordText.text.toString())){
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
                                                } else {
                                                Log.d(ContentValues.TAG, "The document doesn't exist.")
                                            }
                                        }
                                    }
                                //Data to add new transaction
                                val transaction: MutableMap<String, Any> = mutableMapOf()
                                transaction["date"] = LocalDate.now().toString()
                                transaction["id"] = ""
                                transaction["money"] = quantityText.text.toString().toInt()
                                transaction["userEmail"] = email.toString()

                                //add the transaction
                                db.collection("transactions")
                                    .add(transaction)
                                    .addOnSuccessListener {
                                        db.collection("transactions")
                                            .document(it.id)
                                            .update( mapOf(
                                                "id" to it.id
                                            ))
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            activity,
                                            "Failed to add the Credit Card", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                contained = true
                                break
                        }
                    }
                    //If credit card is not in dataBase, we add it
                    if(!contained){
                        //When the card information is correct we add it
                        if(!cardNumberText.text.isEmpty() && cardNumberText.text.length == 12 && cardNumberText.text.matches("[0-9]+".toRegex())
                            && !cardHolderNameText.text.isEmpty()
                            && !dueDateDate.text.isEmpty() && dueDateDate.text.length == 5 && dueDateDate.text.matches("[0-9/]+".toRegex())
                            && !backNumberText.text.isEmpty()  && backNumberText.text.length == 3 && backNumberText.text.matches("[0-9]+".toRegex())
                            && !passwordText.text.isEmpty() && passwordText.text.length == 4 && passwordText.text.matches("[0-9]+".toRegex())){
                                //Data to add Credit Card
                                val creditCard: MutableMap<String, Any> = mutableMapOf()
                                creditCard["backNumber"] = backNumberText.text.toString()
                                creditCard["cardHolderName"] = cardHolderNameText.text.toString()
                                creditCard["cardNumber"] = cardNumberText.text.toString()
                                creditCard["dueDate"] = dueDateDate.text.toString()
                                creditCard["pin"] = passwordText.text.toString()

                                //add the creditCard
                                dbCollection
                                    .add(creditCard)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            activity,
                                            "Card and money successfully added to the system.\nPlease, reStart app to validate the card.", Toast.LENGTH_SHORT
                                        ).show()
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
                                                    } else {
                                                        Log.d(ContentValues.TAG, "The document doesn't exist.")
                                                    }
                                                }
                                            }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            activity,
                                            "Failed to add the Credit Card", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                //Data to add new transaction
                                val email = FirebaseAuth.getInstance().currentUser?.email
                                val transaction: MutableMap<String, Any> = mutableMapOf()
                                transaction["date"] = LocalDate.now().toString()
                                transaction["id"] = ""
                                transaction["money"] = quantityText.text.toString().toInt()
                                transaction["userEmail"] = email.toString()

                                //add the transaction
                                db.collection("transactions")
                                    .add(transaction)
                                    .addOnSuccessListener {
                                        db.collection("transactions")
                                            .document(it.id)
                                            .update( mapOf(
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
                    } else {
                        Toast.makeText(
                            activity,
                            "Money added successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d("ERROR", "Error reading firebase data", documents.exception)
                }
            }

        }
        return v
    }

    //Stores the data of the credit cards
    private fun getCreditCardsData() {
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        db = FirebaseFirestore.getInstance()
        val dbCollection = db.collection("creditCards")
        dbCollection.get().
        addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                for (document in documents.result) {
                    backNumber[document.id] = document.get("backNumber") as String
                    cardHolderName[document.id] = document.get("cardHolderName") as String
                    cardNumber[document.id] = document.get("cardNumber") as String
                    dueDate[document.id] = document.get("dueDate") as String
                    pin[document.id] = document.get("pin") as String
                }
            } else {
                Log.d("ERROR", "Error reading firebase data", documents.exception)
            }
        }
    }
}
