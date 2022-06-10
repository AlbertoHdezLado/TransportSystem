package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarItemView
import com.google.android.material.navigation.NavigationBarMenuView
import com.google.android.material.navigation.NavigationBarView
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
        //Bottom navigation configuration
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_navigation) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigation.setupWithNavController(navController)

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
