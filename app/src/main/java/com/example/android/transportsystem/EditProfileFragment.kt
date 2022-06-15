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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class EditProfileFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionReference : CollectionReference = db.collection("users")

    @SuppressLint("UseSwitchCompatOrMaterialCode", "CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        lateinit var userId: String
        lateinit var pass: String

        val editprofEmail: EditText = v.findViewById(R.id.editprof_email)
        val editprofPassword: EditText = v.findViewById(R.id.editprof_password)
        val editconfProfPassword: EditText = v.findViewById(R.id.editprof_confpassword)

        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        collectionReference.whereEqualTo("email", email).get().addOnCompleteListener { documents ->
            if (documents.isSuccessful) {
                for (document in documents.result) {
                    userId = document.id
                    pass = document.getString("pass").toString()

                    editprofEmail.setText(email)
                    editprofPassword.setText(pass)
                }
            }
        }

        val editButton = v.findViewById<Button>(R.id.editprof_modifybutton)
        editButton.setOnClickListener {
            val newEmail = editprofEmail.text.toString()
            if (email != newEmail) {
                auth.currentUser!!.updateEmail(editprofEmail.text.toString())
                db.collection("users").document(userId).update("email", newEmail)

                db.collection("journeys").whereEqualTo("userEmail", email).get().addOnCompleteListener { documents ->
                    if (documents.isSuccessful) {
                        for (document in documents.result) {
                            db.collection("journeys").document(document.id).update("userEmail", newEmail)
                        }
                    }
                }

                db.collection("transactions").whereEqualTo("userEmail", email).get().addOnCompleteListener { documents ->
                    if (documents.isSuccessful) {
                        for (document in documents.result) {
                            db.collection("transactions").document(document.id).update("userEmail", newEmail)
                        }
                    }
                }
                Toast.makeText(activity, "Email modified successfully!", Toast.LENGTH_SHORT).show()
            }
            val newPass = editprofPassword.text.toString()
            val newConfPass = editconfProfPassword.text.toString()
            if (pass != newPass) {
                if (newPass == newConfPass) {
                    auth.currentUser!!.updatePassword(editprofPassword.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity,
                                    "Data has been updated",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    db.collection("users").document(userId).update("pass", newPass)
                    Toast.makeText(activity, "Passwords modified successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Passwords are not the same!", Toast.LENGTH_SHORT).show()
                }
            }
            if (email == newEmail && pass == newPass)
                Toast.makeText(activity, "Nothing have been changed", Toast.LENGTH_SHORT).show()
            else {
                Firebase.auth.signOut()
                val intent = Intent(activity, InitActivity::class.java)
                startActivity(intent)
            }
        }


        return v
    }

}