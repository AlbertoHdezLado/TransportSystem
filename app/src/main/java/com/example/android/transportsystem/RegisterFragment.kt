package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.android.shelted.Classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterFragment : Fragment() {

    lateinit var name: EditText
    lateinit var surname: EditText
    lateinit var date: TextView
    lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confpassword: EditText
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_register, container, false)

        // Initialising auth object
        auth = FirebaseAuth.getInstance()

        //Getting components
        val selectDateButton = v.findViewById<Button>(R.id.reg_selectdatebutton)
        val regButton = v.findViewById<Button>(R.id.reg_regbutton)

        name = v.findViewById(R.id.reg_name)
        surname = v.findViewById(R.id.reg_surname)
        date = v.findViewById(R.id.reg_datetext)
        email = v.findViewById(R.id.reg_email)
        password = v.findViewById(R.id.reg_password)
        confpassword = v.findViewById(R.id.reg_confpassword)

        //Calendar variables
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        //Select date button onClickListener
        selectDateButton.setOnClickListener {
            val dpd = DatePickerDialog(v.context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                date.text = "$dayOfMonth/${monthOfYear+1}/$year"
            }, year, month, day)
            dpd.show()
        }

        //Register button onClickListener
        regButton.setOnClickListener {
            signUpUser()
        }

        return v
    }

    private fun signUpUser() {
        //Variables
        val nameText = name.text.toString()
        val surnameText = surname.text.toString()
        val dateText = date.text.toString()
        val emailText = email.text.toString()
        val passwordText = password.text.toString()
        val confpasswordText = confpassword.text.toString()

        // Check empty variables
        if (emailText.isBlank() || passwordText.isBlank() || confpasswordText.isBlank() || nameText.isBlank() || dateText.isBlank()) {
            Toast.makeText(activity, "Data can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        //Check password confirmation
        if (passwordText != confpasswordText) {
            Toast.makeText(activity, "Password and Confirm Password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }

        //Create user
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(activity, "Successful registration!", Toast.LENGTH_SHORT).show()

                //Save user data in firebase
                val userBD = User(nameText, surnameText, dateText, emailText, passwordText)
                val rootRef = FirebaseFirestore.getInstance()
                val usersRef = rootRef.collection("users")
                usersRef.document(emailText).set(userBD)

                //Change fragment to login
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(activity,
                    "Your email is already registered!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}