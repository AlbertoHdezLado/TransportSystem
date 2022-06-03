package com.example.android.transportsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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