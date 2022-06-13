package com.example.android.transportsystem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")

    @SuppressLint("UseSwitchCompatOrMaterialCode", "CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        lateinit var pass: String
        lateinit var name: String
        lateinit var surname: String
        lateinit var birthdate: String

        val profName: TextView = v.findViewById(R.id.prof_name)
        val profSurname: TextView = v.findViewById(R.id.prof_surname)
        val profBirthdate: TextView = v.findViewById(R.id.prof_birthdate)
        val profEmail: TextView = v.findViewById(R.id.prof_email)
        val profPassword: TextView = v.findViewById(R.id.prof_password)

        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        collectionReference.whereEqualTo("email", email).get().addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                println(email)
                for (document in documents.result) {
                    name = document.getString("name").toString()
                    surname = document.getString("surname").toString()
                    birthdate = document.getString("birthdate").toString()
                    pass = document.getString("pass").toString()


                    profName.text = name
                    profSurname.text = surname
                    profBirthdate.text = birthdate
                    profEmail.text = email
                    profPassword.text = pass
                    profPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD;
                }
            }
        }

        val editProfileButton = v.findViewById<Button>(R.id.prof_editprofilebutton)
        editProfileButton.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditProfile()
            findNavController().navigate(action)
        }

        val logoutButton = v.findViewById<Button>(R.id.prof_logoutbutton)
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(activity, "Logged-out successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, InitActivity::class.java)
            startActivity(intent)
        }

        return v
    }

}