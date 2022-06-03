package com.example.android.transportsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class WalletFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_wallet, container, false)

        val addMoneybutton = v.findViewById<Button>(R.id.wallet_addmoneybutton)
        val checkTransactionsbutton = v.findViewById<Button>(R.id.wallet_checktransactionsbutton)

        addMoneybutton.setOnClickListener {
            val action = WalletFragmentDirections.actionWalletFragmentToAddMoneyFragment()
            findNavController().navigate(action)
        }

        checkTransactionsbutton.setOnClickListener {
            val action = WalletFragmentDirections.actionWalletFragmentToTransactionListFragment()
            findNavController().navigate(action)
        }

        return v
    }

}