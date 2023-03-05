package com.example.contactmanagerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactmanagerapp.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var binding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // What happens when Sign Up button is clicked
        binding.btnSignUp.setOnClickListener{
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPass.text.toString().trim()

            // Checking if all fields are filled and sending toast if not
            if(username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                // If all fields are filled registering user
                registerUser(username, email, password)
            }else if(username.isEmpty()) {
                Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show()
            }else if(email.isEmpty()){
                Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show()
            }

            // Clearing all text fields
            binding.etUsername.text?.clear()
            binding.etEmail.text?.clear()
            binding.etPass.text?.clear()
        }

        // If user already has an account, clicking the text view redirects user to Sign In page
        binding.tvSignIn.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java )
            startActivity(signInIntent)
        }
    }

    // Function to register new user
    private fun registerUser(username: String, email: String, password: String) {
        val user = User(email, password, username) // Making user object to store in DB
        // Going to 'Users' path in DB
        database = FirebaseDatabase.getInstance().getReference("Users")
        // Checking if username already exists or not
        database.child(username).get().addOnSuccessListener {
            if(it.exists()){
                Toast.makeText(this, "Username is already registered", Toast.LENGTH_SHORT).show()
            }else{
                // If username doesn't exits then adding a new user in the DB
                database.child(username).setValue(user).addOnSuccessListener {
                    Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                    // After adding user redirecting user to the Sign In page
                    val signInIntent = Intent(this, SignInActivity :: class.java)
                    startActivity(signInIntent)
                }.addOnFailureListener {
                    Toast.makeText(this, "Servers are down. Please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Servers are down. Please try again later", Toast.LENGTH_SHORT).show()
        }
    }
}