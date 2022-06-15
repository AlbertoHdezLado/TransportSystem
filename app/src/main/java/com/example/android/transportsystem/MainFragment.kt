package com.example.android.transportsystem

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_main, container, false)

        val payButton = v.findViewById<Button>(R.id.main_paybutton)
        val journeysButton = v.findViewById<Button>(R.id.main_journeysbutton)

        payButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToPayFragment()
            findNavController().navigate(action)
        }

        journeysButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToJourneyListFragment()
            findNavController().navigate(action)
        }

        return v
    }


}