package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val name = findViewById<TextView>(R.id.main_currentName)
        val money = findViewById<TextView>(R.id.main_currentMoney)

        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")
        usersRef.document(email!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    name.text = "${document.getString("name").toString()} ${document.getString("surname").toString()}"
                    money.text = "${document.getDouble("money").toString()} pln"
                } else {
                    Log.d(TAG, "The document doesn't exist.")
                }
            } else {
                task.exception?.message?.let {
                    Log.d(TAG, "Error in task")
                }
            }
        }


    }

    //To override onBackPressed on MainFragment
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment
        val fragment = navHostFragment!!.childFragmentManager.fragments[0]
        if (fragment is MainFragment)
            moveTaskToBack(true)
        else
            super.onBackPressed()
    }

}