package com.example.android.transportsystem

import android.annotation.SuppressLint
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
                println(email)
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
            var newEmail = editprofEmail.text.toString()
            if (email != newEmail) {
                auth.currentUser!!.updateEmail(editprofEmail.text.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Data has been updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                db.collection("users").document(userId).update("email", newEmail)

                db.collection("journeys").whereEqualTo("userEmail", email).get().addOnCompleteListener { documents ->
                    if (documents.isSuccessful) {
                        for (document in documents.result) {
                            if (document.getString("email").toString() == email)
                                db.collection("journeys").document(document.id).update("userEmail", newEmail)
                        }
                    }
                }

                val action = EditProfileFragmentDirections.actionEditProfileToProfileFragment()
                findNavController().navigate(action)
            }
        }


        return v
    }

}