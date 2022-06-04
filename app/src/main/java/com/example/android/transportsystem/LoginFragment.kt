package com.example.android.transportsystem

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = v.findViewById<Button>(R.id.log_loginbutton)
        val registerButton = v.findViewById<Button>(R.id.log_registerbutton)
        val email = v.findViewById<EditText>(R.id.log_emailtext)
        val password = v.findViewById<EditText>(R.id.log_passwordtext)

        loginButton.setOnClickListener {
            val txt_email: String = email.text.toString()
            val txt_password: String = password.text.toString()

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(activity, "Empty Credentials!", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Logged-in successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(activity,
                            "Your email or password doesn't exist!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }

        registerButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        return v
    }
}