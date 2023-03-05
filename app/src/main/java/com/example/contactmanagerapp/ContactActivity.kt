package com.example.contactmanagerapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.contactmanagerapp.databinding.ActivityContactBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ContactActivity : AppCompatActivity() {

    // All lateinit variables declaration
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityContactBinding
    private lateinit var dialog: Dialog

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Getting username from last Activity i.e. SignInActivity
        val username = intent.getStringExtra(SignInActivity.KEY1)
        binding.textView.text = "Welcome $username"

        // What happens on clicking Add Button
        binding.btnAdd.setOnClickListener {

            val name = binding.etName.text.toString().trim() // Using trim to remove extra white spaces from beginning and end of the string
            val email = binding.etEmail.text.toString().trim()
            val mobNo = binding.etMobNo.text.toString().trim()

            // Checking if all fields are filled
            if (name.isNotEmpty() && email.isNotEmpty() && mobNo.isNotEmpty() && mobNo.length == 10) {
                // If all fields are valid then addContact function adds contact info in DB
                addContact(username.toString(), name, email, mobNo)
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            } else if (mobNo.isEmpty()) {
                Toast.makeText(this, "Please enter Mobile No.", Toast.LENGTH_SHORT).show()
            } else if (mobNo.length != 10) {
                // Checking for valid mobile number
                Toast.makeText(this, "Please enter a valid Mobile No.", Toast.LENGTH_SHORT).show()
                binding.etMobNo.text?.clear()
            }
        }

        // Initializing custom dialogue box
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_box)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.rounded_bg_grey)) // Setting dialog box background

        // What happens when dialog box button is clicked
        val btnOK = dialog.findViewById<Button>(R.id.btnOK)
        btnOK.setOnClickListener {
            dialog.dismiss()
        }
    }

    // Function to add contact info in DB
    private fun addContact(username: String, name: String, email: String, mobNo: String) {
        // Going to 'Contacts' node which is unique to each particular user
        database = FirebaseDatabase.getInstance().getReference("Users/$username/Contacts")
        // Checking if mobNo already exists or not
        database.child(mobNo).get().addOnSuccessListener {
            if (it.exists()) {
                Toast.makeText(this, "Mobile No. is already registered", Toast.LENGTH_SHORT).show()
            } else {
                // Adding contact info if mobile no. doesn't already exists
                val contact = Contact(email, mobNo, name) // Making contact object
                database.child(mobNo).setValue(contact).addOnSuccessListener {
                    // Clearing all fields if contact info added successfully
                    binding.etName.text?.clear()
                    binding.etEmail.text?.clear()
                    binding.etMobNo.text?.clear()
                    dialog.show() // Show custom dialog box
                }.addOnFailureListener {
                    Toast.makeText(this, "Servers are down. Please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Servers are down. Please try again later", Toast.LENGTH_SHORT).show()
        }
    }
}