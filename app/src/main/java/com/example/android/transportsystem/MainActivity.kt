package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")
    //private var myTrans = supportFragmentManager.beginTransaction()
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
        usersRef.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
            for (document in documents) {
                if (document.exists()) {
                    name.text = "${document.getString("name").toString()} ${document.getString("surname").toString()}"
                    money.text = "${document.getDouble("money").toString()} pln"
                } else {
                    Log.d(TAG, "The document doesn't exist.")
                }
            }
        }
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener(navigationListener)
    }

    //Travel using the bottom Navigation
    private val navigationListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {

                }
                R.id.menu_home -> {

                }
                R.id.menu_settings -> {
                }
            }
            //myTrans.commit()
            false
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
