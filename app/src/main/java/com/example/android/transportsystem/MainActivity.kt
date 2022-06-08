package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.android.transportsystem.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


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
                    val fragment = ProfileFragment()
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_navigation,fragment)
                        .commit()
                   // myTrans.replace(R.id.main_navigation, fragment)
                }
                R.id.menu_home -> {
                    val fragment = MainFragment()
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_navigation,fragment)
                        .commit()
                    //myTrans.replace(R.id.main_navigation, fragment)
                }
                R.id.menu_settings -> {
                    val fragment = SettingFragment()
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_navigation,fragment)
                        .commit()
                    //myTrans.replace(R.id.main_navigation, fragment)
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
