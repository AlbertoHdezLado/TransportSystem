package com.example.android.transportsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)
        val log_login_button = v.findViewById<Button>(R.id.reg_registerbutton)
        log_login_button.setOnClickListener{
            val navHostFragment =
                activity!!.supportFragmentManager.findFragmentById(R.id.init_navigation) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.registerFragment)

        }

        return v
    }
}